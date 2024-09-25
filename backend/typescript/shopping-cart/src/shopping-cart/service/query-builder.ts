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

    buildId({ id, withItems }: FindByIdParams) {
        const queryBuilder = this.#shoppingCartRepository.createQueryBuilder(
            this.#shoppingCartAlias,
        );

        if (withItems) {
            queryBuilder.leftJoinAndSelect(
                `${this.#shoppingCartAlias}.cartItems`, // Korrektur hier
                this.#itemAlias,
            );
        }

        queryBuilder.where(`${this.#shoppingCartAlias}.id = :id`, {
            id,
        });
        return queryBuilder;
    }

    build(withItems: boolean | false) {
        this.#logger.debug(
            'build: withItems=%s', withItems);

        let queryBuilder = this.#shoppingCartRepository.createQueryBuilder(
            this.#shoppingCartAlias,
        );

        if (withItems) {
            queryBuilder.leftJoinAndSelect(
                `${this.#shoppingCartAlias}.cartItems`, // Korrektur hier
                this.#itemAlias,
            );
        }

        let useWhere = true;


        this.#logger.debug('build: sql=%s', queryBuilder.getSql());
        return queryBuilder;
    }

    // toString Methode hinzufügen
    toString(): string {
        return `ShoppingCartQueryBuilder {
            shoppingCartAlias: ${this.#shoppingCartAlias},
            itemAlias: ${this.#itemAlias}
        }`;
    }
}
