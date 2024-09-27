import { getLogger } from '../../logger/logger';
import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, SelectQueryBuilder } from 'typeorm';
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

    // Verbesserte build-Methode
    build(withItems: boolean = false, ...props: SearchCriteria[]): SelectQueryBuilder<ShoppingCart> {
        this.#logger.debug('build: withItems=%s', withItems);

        let queryBuilder = this.#shoppingCartRepository.createQueryBuilder(this.#shoppingCartAlias);

        if (withItems) {
            queryBuilder.leftJoinAndSelect(
                `${this.#shoppingCartAlias}.cartItems`,
                this.#itemAlias
            );
        }

        // Verwenden Sie Array.reduce für eine klarere Implementierung
        props.reduce((qb, criteria, index) => {
            return Object.entries(criteria).reduce((acc, [key, value]) => {
                if (value !== undefined && value !== null) {
                    const param = { [key]: value };
                    return index === 0 && acc === qb
                        ? acc.where(`${this.#shoppingCartAlias}.${key} = :${key}`, param)
                        : acc.andWhere(`${this.#shoppingCartAlias}.${key} = :${key}`, param);
                }
                return acc;
            }, qb);
        }, queryBuilder);

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
