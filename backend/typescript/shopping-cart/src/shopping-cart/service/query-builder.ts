import { getLogger } from '../../logger/logger';
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { SearchCriteria } from '../model/searchCriteria';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { Item } from '../model/entity/item.entity';
import { FindByIdParams } from './shopping-cart-read.service';

@Injectable()
export class ShoppingCartQueryBuilder {
    readonly #shoppingCartRepository: Repository<ShoppingCart>;
    readonly #logger = getLogger(ShoppingCartQueryBuilder.name);

    readonly #shoppingCartAlias = `${ShoppingCart.name

    .charAt(0)
        .toLowerCase()}${ShoppingCart.name.slice(1)}`;

    readonly #itemAlias = `${Item.name
        .charAt(0)
        .toLowerCase()}${Item.name.slice(1)}`;

    constructor(@InjectRepository(ShoppingCart) repo: Repository<ShoppingCart>) {
        this.#shoppingCartRepository = repo;
    }

    buildId({ shoppingCartId, withItems }: FindByIdParams) {
        const queryBuilder = this.#shoppingCartRepository.createQueryBuilder(
            this.#shoppingCartAlias,
        );

        if (withItems) {
            queryBuilder.leftJoinAndSelect(
                `${this.#shoppingCartAlias}.items`,
                this.#itemAlias,
            );
        }

        queryBuilder.where(`${this.#shoppingCartAlias}.shoppingCartId = :shoppingCartId`, {
            shoppingCartId,
        });
        return queryBuilder;
    }

    build(
        {
            ...props
        }: SearchCriteria,
        withItems: boolean | false,
    ) {
        this.#logger.debug(
            'build: props=%o , withItems=%s',
            props,
            withItems,
        );

        let queryBuilder = this.#shoppingCartRepository.createQueryBuilder(
            this.#shoppingCartAlias,
        );

        if (withItems) {
            queryBuilder.leftJoinAndSelect(
                `${this.#shoppingCartAlias}.contacts`,
                this.#itemAlias,
            );
        }

        let useWhere = true;


        Object.keys(props).forEach((key) => {
            const param: Record<string, any> = {};
            param[key] = (props as Record<string, any>)[key];
            queryBuilder = useWhere
                ? queryBuilder.where(`${this.#shoppingCartAlias}.${key} = :${key}`, param)
                : queryBuilder.andWhere(
                    `${this.#shoppingCartAlias}.${key} = :${key}`,
                    param,
                );
            useWhere = false;
        });

        this.#logger.debug('build: sql=%s', queryBuilder.getSql());
        return queryBuilder;
    }
}
