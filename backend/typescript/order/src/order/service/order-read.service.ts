import { Injectable, NotFoundException } from '@nestjs/common';
import { Order } from './../entity/order.entity.js';
import { QueryBuilder } from './query-builder.js';

import { getLogger } from '../../logger/logger.js';
import { type SearchCriteria } from '../model/searchCriteria.js';


export interface FindByIdParams {
    readonly id: string;
    readonly withOrderedItem?: boolean;
}


@Injectable()
export class OrderReadService {
    static readonly ID_PATTERN = /^[1-9]\d{0,10}$/u;

    readonly #orderProps: string[];

    readonly #queryBuilder: QueryBuilder;

    readonly #logger = getLogger(OrderReadService.name);

    constructor(queryBuilder: QueryBuilder) {
        const orderDummy = new Order();
        this.#orderProps = Object.getOwnPropertyNames(orderDummy);
        this.#queryBuilder = queryBuilder;
    }

    async findById({ id, withOrderedItem = false }: FindByIdParams) {
        this.#logger.debug('findById: id=%d', id);

        const order = await this.#queryBuilder
            .buildId({ id, withOrderedItem })
            .getOne();
        if (order === null) {
            throw new NotFoundException(`Es gibt kein Order mit der ID ${id}.`);
        }

        if (this.#logger.isLevelEnabled('debug')) {
            this.#logger.debug(
                'findById: order=%s',
                order.toString(),
            );
            if (withOrderedItem) {
                this.#logger.debug(
                    'findById: abbildungen=%o',
                    order.abbildungen,
                );
            }
        }
        return order;
    }

    async find(searchCriteria?: SearchCriteria) {
        this.#logger.debug('find: suchkriterien=%o', searchCriteria);

        // Keine Suchkriterien?
        if (searchCriteria === undefined) {
            return this.#queryBuilder.build({}).getMany();
        }
        const keys = Object.keys(searchCriteria);
        if (keys.length === 0) {
            return this.#queryBuilder.build(searchCriteria).getMany();
        }

        // Falsche Namen fuer Suchkriterien?
        if (!this.#checkKeys(keys)) {
            throw new NotFoundException('Ungueltige Suchkriterien');
        }


        const orders = await this.#queryBuilder.build(searchCriteria).getMany();
        if (orders.length === 0) {
            this.#logger.debug('find: Keine Buecher gefunden');
            throw new NotFoundException(
                `Keine Buecher gefunden: ${JSON.stringify(searchCriteria)}`,
            );
        }
        this.#logger.debug('find: buecher=%o', orders);
        return orders;
    }

    #checkKeys(keys: string[]) {
        let validKeys = true;
        keys.forEach((key) => {
            if (
                !this.#orderProps.includes(key)
            ) {
                this.#logger.debug(
                    '#checkKeys: ungueltiges Suchkriterium "%s"',
                    key,
                );
                validKeys = false;
            }
        });

        return validKeys;
    }
}
