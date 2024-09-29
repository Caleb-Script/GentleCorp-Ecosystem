import { type DeleteResult, Repository } from 'typeorm';
import { Injectable, NotFoundException } from '@nestjs/common';
import {
    IsbnExistsException,
    VersionInvalidException,
    VersionOutdatedException,
} from './exceptions.js';
import { Order } from '../model/entity/order.entity.js';
import { OrderReadService } from './order-read.service.js';
import { InjectRepository } from '@nestjs/typeorm';
import { MailService } from '../../mail/mail.service.js';
import { getLogger } from '../../logger/logger.js';

export interface UpdateParams {
    readonly id: number | undefined;
    readonly order: Order;
    readonly version: string;
}


@Injectable()
export class OrderWriteService {
    private static readonly VERSION_PATTERN = /^"\d{1,3}"/u;

    readonly #repo: Repository<Order>;

    readonly #readService: OrderReadService;

    readonly #mailService: MailService;

    readonly #logger = getLogger(OrderWriteService.name);

    constructor(
        @InjectRepository(Order) repo: Repository<Order>,
        readService: OrderReadService,
        mailService: MailService,
    ) {
        this.#repo = repo;
        this.#readService = readService;
        this.#mailService = mailService;
    }

    async create(order: Order): Promise<number> {
        this.#logger.debug('create: order=%o', order);


        const orderDb = await this.#repo.save(order); // implizite Transaktion
        this.#logger.debug('create: orderDb=%o', orderDb);

        await this.#sendmail(orderDb);

        return orderDb.id!;
    }

    async update({ id, order, version }: UpdateParams): Promise<number> {
        this.#logger.debug(
            'update: id=%d, order=%o, version=%s',
            id,
            order,
            version,
        );
        if (id === undefined) {
            this.#logger.debug('update: Keine gueltige ID');
            throw new NotFoundException(`Es gibt kein Order mit der ID ${id}.`);
        }

        const validateResult = await this.#validateUpdate(order, id, version);
        this.#logger.debug('update: validateResult=%o', validateResult);
        if (!(validateResult instanceof Order)) {
            return validateResult;
        }

        const orderNeu = validateResult;
        const merged = this.#repo.merge(orderNeu, order);
        this.#logger.debug('update: merged=%o', merged);
        const updated = await this.#repo.save(merged); // implizite Transaktion
        this.#logger.debug('update: updated=%o', updated);

        return updated.version!;
    }




    async #sendmail(order: Order) {
        const subject = `Neues Order ${order.id}`;
        const orderNumber = order.orderNumber;
        const body = `Das Order mit dem Titel <strong>${orderNumber}</strong> ist angelegt`;
        await this.#mailService.sendmail({ subject, body });
    }

    async #validateUpdate(
        order: Order,
        id: number,
        versionStr: string,
    ): Promise<Order> {
        this.#logger.debug(
            '#validateUpdate: order=%o, id=%s, versionStr=%s',
            order,
            id,
            versionStr,
        );
        if (!OrderWriteService.VERSION_PATTERN.test(versionStr)) {
            throw new VersionInvalidException(versionStr);
        }

        const version = Number.parseInt(versionStr.slice(1, -1), 10);
        this.#logger.debug(
            '#validateUpdate: order=%o, version=%d',
            order,
            version,
        );

        const orderDb = await this.#readService.findById({ id });

        // nullish coalescing
        const versionDb = orderDb.version!;
        if (version < versionDb) {
            this.#logger.debug('#validateUpdate: versionDb=%d', version);
            throw new VersionOutdatedException(version);
        }
        this.#logger.debug('#validateUpdate: orderDb=%o', orderDb);
        return orderDb;
    }
}
