import { type DeleteResult, Repository } from 'typeorm';
import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { getLogger } from '../../logger/logger.js';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { ShoppingCartReadService } from './shopping-cart-read.service.js';
import { Item } from '../model/entity/item.entity.js';
import {
    VersionInvalidException,
    VersionOutdatedException,
} from './exceptions';
import { ConsumerService } from '../../kafka/Consumer.service.js';


export interface UpdateParams {
    readonly shoppingCartId: string | undefined;
    readonly shoppingCart: ShoppingCart;
    readonly version: string;
}

@Injectable()
export class ShoppingCartWriteService {
    private static readonly VERSION_PATTERN = /^"\d{1,3}"/u;
    readonly #repo: Repository<ShoppingCart>;
    readonly #readService: ShoppingCartReadService;
    readonly #logger = getLogger(ShoppingCartWriteService.name);

    constructor(
      @InjectRepository(ShoppingCart)
      repo: Repository<ShoppingCart>,
      private readonly consumerService: ConsumerService,
      readService: ShoppingCartReadService,
    ) {
        this.#repo = repo;
        this.#readService = readService;
    }

    async create(shoppingCart: ShoppingCart): Promise<string> {
        this.#logger.debug('create: shoppingCart=%o', shoppingCart);
        const shoppingCartDb = await this.#repo.save(shoppingCart);
        this.#logger.debug('create: shoppingCartDb=%o', shoppingCartDb);
        return shoppingCartDb.shoppingCartId!;
    }

    async update({ shoppingCartId, shoppingCart, version }: UpdateParams): Promise<number> {
        this.#logger.debug(
            'update: id=%d, shoppingCart=%o, version=%s',
            shoppingCartId,
            shoppingCart,
            version,
        );
        if (shoppingCartId === undefined) {
            this.#logger.debug('update: Keine gueltige ID');
            throw new NotFoundException(`Es gibt kein ShoppingCart mit der ID ${shoppingCartId}.`);
        }

        const validateResult = await this.#validateUpdate(shoppingCart, shoppingCartId, version);
        this.#logger.debug('update: validateResult=%o', validateResult);
        if (!(validateResult instanceof ShoppingCart)) {
            return validateResult;
        }

        const shoppingCartNeu = validateResult;
        const merged = this.#repo.merge(shoppingCartNeu, shoppingCart);
        this.#logger.debug('update: merged=%o', merged);
        const updated = await this.#repo.save(merged); // implizite Transaktion
        this.#logger.debug('update: updated=%o', updated);

        return updated.version!;
    }

    async delete(shoppingCartId: string) {
        this.#logger.debug('delete: id=%d', shoppingCartId);
        const shoppingCart = await this.#readService.findById({ shoppingCartId, withItems: true });

        let deleteResult: DeleteResult | undefined;
        await this.#repo.manager.transaction(async (transactionalMgr) => {
            // Das ShoppingCart zur gegebenen ID mit Titel und Abb. asynchron loeschen

            // TODO "cascade" funktioniert nicht beim Loeschen
            const items = shoppingCart.cartItems ?? [];
            for (const abbildung of items) {
                await transactionalMgr.delete(Item, abbildung.id);
            }

            deleteResult = await transactionalMgr.delete(ShoppingCart, shoppingCartId);
            this.#logger.debug('delete: deleteResult=%o', deleteResult);
        });

        return (
            deleteResult?.affected !== undefined &&
            deleteResult.affected !== null &&
            deleteResult.affected > 0
        );
    }

    async #validateUpdate(
        shoppingCart: ShoppingCart,
        shoppingCartId: string,
        versionStr: string,
    ): Promise<ShoppingCart> {
        this.#logger.debug(
            '#validateUpdate: shoppingCart=%o, id=%s, versionStr=%s',
            shoppingCart,
            shoppingCartId,
            versionStr,
        );
        if (!ShoppingCartWriteService.VERSION_PATTERN.test(versionStr)) {
            throw new VersionInvalidException(versionStr);
        }

        const version = Number.parseInt(versionStr.slice(1, -1), 10);
        this.#logger.debug(
            '#validateUpdate: shoppingCart=%o, version=%d',
            shoppingCart,
            version,
        );

        const shoppingCartDb = await this.#readService.findById({ shoppingCartId, withItems:false });

        // nullish coalescing
        const versionDb = shoppingCartDb.version!;
        if (version < versionDb) {
            this.#logger.debug('#validateUpdate: versionDb=%d', version);
            throw new VersionOutdatedException(version);
        }
        this.#logger.debug('#validateUpdate: shoppingCartDb=%o', shoppingCartDb);
        return shoppingCartDb;
    }
}
