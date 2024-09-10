import { Injectable, NotFoundException } from '@nestjs/common';
import { getLogger } from '../../logger/logger';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { ShoppingCartQueryBuilder } from './query-builder';
import { SearchCriteria } from '../model/searchCriteria';


export interface FindByIdParams {
    readonly shoppingCartId: string;
    readonly withItems: boolean | undefined;
}

export interface FindParams {
    readonly searchCriteria?: SearchCriteria;
}

@Injectable()
export class ShoppingCartReadService {
    static readonly ID_PATTERN =
        /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/u;

    readonly #shoppingCartProps: string[];
    readonly #queryBuilder: ShoppingCartQueryBuilder;
    readonly #logger = getLogger(ShoppingCartReadService.name);

    constructor(queryBuilder: ShoppingCartQueryBuilder) {
        const shoppingCartDummy = new ShoppingCart();
        this.#shoppingCartProps = Object.getOwnPropertyNames(shoppingCartDummy);
        this.#queryBuilder = queryBuilder;
    }

    async findById({ shoppingCartId, withItems }: FindByIdParams) {
        this.#logger.debug('findById: id=%s, withItems=%s', shoppingCartId, withItems);

        if (shoppingCartId === undefined || shoppingCartId === null) {
            throw new NotFoundException('Shopping ShoppingCart with ID %s not found', shoppingCartId);
        }

        const cart: ShoppingCart = await this.#queryBuilder
            .buildId({ shoppingCartId, withItems })
            .getOne();

        if (cart === null) {
            throw new NotFoundException(`No cart found with ID ${shoppingCartId}.`);
        }

        this.#logger.debug('findById: cart=%s', cart);
        return cart;
    }

    async find({ searchCriteria }: FindParams) {
        const withItems = false;
        this.#logger.debug(
            'find: searchCriteria=%o,withItems=%s',
            searchCriteria,
            withItems,
        );

        if (searchCriteria === undefined) {
            const carts: ShoppingCart[] = await this.#queryBuilder
                .build({}, withItems)
                .getMany();


            this.#logger.debug('find: carts=%o', carts);
            return carts;
        }

        const keys: string[] = Object.keys(searchCriteria);
        if (keys.length === 0) {
            return this.#queryBuilder.build(searchCriteria, withItems).getMany();
        }

        const carts: ShoppingCart[] = await this.#queryBuilder
            .build(searchCriteria, withItems)
            .getMany();

        this.#logger.debug('find: carts=%o', carts);
        return carts;
    }
}
