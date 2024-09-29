import { Args, Query, Resolver } from '@nestjs/graphql';
import { UseFilters, UseInterceptors } from '@nestjs/common';
import { Order } from '../model/entity/order.entity.js';
import { OrderReadService } from '../service/order-read.service.js';
import { HttpExceptionFilter } from './http-exception.filter.js';
import { Roles } from 'nest-keycloak-connect';
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor.js';
import { type SearchCriteria } from '../model/searchCriteria.js';
import { getLogger } from '../../logger/logger.js';

export interface IdInput {
    readonly id: string;
}

export interface SearchCriteriaInput {
    readonly searchCriteria?: SearchCriteria;
}

@Resolver(() => Order)
@UseFilters(HttpExceptionFilter)
@UseInterceptors(ResponseTimeInterceptor)
export class OrderQueryResolver {
    readonly #orderReadService: OrderReadService;
    readonly #logger = getLogger(OrderQueryResolver.name);

    constructor(orderReadService: OrderReadService) {
        this.#orderReadService = orderReadService;
    }

    @Query(() => Order, { nullable: true })
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user', 'gentlecorp-customer'] })
    async order(@Args('id') id: string) {
        this.#logger.debug('findById: id=%s', id);
        const order = await this.#orderReadService.findById({ id, withOrderedItem: true });

        if (this.#logger.isLevelEnabled('debug')) {
            this.#logger.debug('findById: order=%s', order?.toString());
        }
        return order;
    }

    @Query(() => [Order])
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user'] })
    async orders(@Args('searchCriteria', { nullable: true }) searchCriteria?: SearchCriteria) {
        this.#logger.debug('find: searchCriteria=%o', searchCriteria);
        const orders = await this.#orderReadService.find(searchCriteria);
        this.#logger.debug('find: orders=%o', orders);
        return orders;
    }
}