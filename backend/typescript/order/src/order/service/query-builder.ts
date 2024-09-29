import { Item } from '../model/entity/item.entity.js';
import { Order } from '../model/entity/order.entity.js';
import { InjectRepository } from '@nestjs/typeorm';
import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { type SearchCriteria } from '../model/searchCriteria.js';
import { getLogger } from '../../logger/logger.js';
import { typeOrmModuleOptions } from '../../config/typeormOptions.js';

export interface BuildIdParams {
    readonly id: number;
    readonly withOrderedItem?: boolean;
}

@Injectable()
export class QueryBuilder {
    readonly #orderAlias = `${Order.name
        .charAt(0)
        .toLowerCase()}${Order.name.slice(1)}`;

    readonly #itemAlias = `${Item.name
        .charAt(0)
        .toLowerCase()}${Item.name.slice(1)}`;

    readonly #repo: Repository<Order>;

    readonly #logger = getLogger(QueryBuilder.name);

    constructor(@InjectRepository(Order) repo: Repository<Order>) {
        this.#repo = repo;
    }

    buildId({ id, withOrderedItem = false }: BuildIdParams) {
        const queryBuilder = this.#repo.createQueryBuilder(this.#orderAlias);

        if (withOrderedItem) {
            queryBuilder.leftJoinAndSelect(
                `${this.#orderAlias}.items`,
                this.#itemAlias,
            );
        }

        queryBuilder.where(`${this.#orderAlias}.id = :id`, { id: id });
        return queryBuilder;
    }

    build({ ...props }: SearchCriteria) {
        this.#logger.debug(
            'build: props=%o',
            props,
        );

        let queryBuilder = this.#repo.createQueryBuilder(this.#orderAlias);



        let useWhere = true;

        Object.keys(props).forEach((key) => {
            const param: Record<string, any> = {};
            param[key] = (props as Record<string, any>)[key];
            queryBuilder = useWhere
                ? queryBuilder.where(
                      `${this.#orderAlias}.${key} = :${key}`,
                      param,
                  )
                : queryBuilder.andWhere(
                      `${this.#orderAlias}.${key} = :${key}`,
                      param,
                  );
            useWhere = false;
        });

        this.#logger.debug('build: sql=%s', queryBuilder.getSql());
        return queryBuilder;
    }
}
