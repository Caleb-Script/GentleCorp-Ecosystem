import { Args, Mutation, Resolver } from '@nestjs/graphql';
import { AuthGuard, Roles } from 'nest-keycloak-connect';
import { UseFilters, UseGuards, UseInterceptors } from '@nestjs/common';
import { OrderDTO, OrderUpdateDTO } from '../model/dto/orderDTO.entity.js';
import { OrderWriteService } from '../service/order-write.service.js';
import { HttpExceptionFilter } from './http-exception.filter';
import { type IdInput } from './order-query.resolver.js';
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor.js';
import { getLogger } from '../../logger/logger.js';
import { type Item } from '../model/entity/item.entity.js';
import { ItemDTO } from '../model/dto/itemDTO.entity.js';
import { type Order } from '../model/entity/order.entity.js';

export interface CreatePayload {
    readonly id: number;
}

export interface UpdatePayload {
    readonly version: number;
}


@Resolver()
@UseGuards(AuthGuard)
@UseFilters(HttpExceptionFilter)
@UseInterceptors(ResponseTimeInterceptor)
export class OrderMutationResolver {
    readonly #orederWriteService: OrderWriteService;

    readonly #logger = getLogger(OrderMutationResolver.name);

    constructor(orderWriteService: OrderWriteService) {
        this.#orederWriteService = orderWriteService;
    }

    @Mutation()
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-customer'] })
    async create(@Args('input') orderDTO: OrderDTO) {
        this.#logger.debug('create: orderDTO=%o', orderDTO);
        const order = this.#orderDtoToOrder(orderDTO);
        const id = await this.#orederWriteService.create(order);
        // TODO BadUserInputError
        this.#logger.debug('createOrder: id=%d', id);
        const payload: CreatePayload = { id };
        return payload;
    }

    @Mutation()
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user'] })
    async update(@Args('input') orderDTO: OrderUpdateDTO) {
        this.#logger.debug('update: order=%o', orderDTO);

        const order = this.#orderUpdateDtoToOrder(orderDTO);
        const versionStr = `"${orderDTO.version.toString()}"`;

        const versionResult = await this.#orederWriteService.update({
            id: Number.parseInt(orderDTO.id, 10),
            order,
            version: versionStr,
        });
        // TODO BadUserInputError
        this.#logger.debug('updateOrder: versionResult=%d', versionResult);
        const payload: UpdatePayload = { version: versionResult };
        return payload;
    }

    #orderDtoToOrder(orderDTO: OrderDTO): Order {
        const orderedItems: Item[] = orderDTO.items.map((itemDTO: ItemDTO) => {
            const item: Item = {
                id: undefined,
                skuCode: itemDTO.skuCode,
                price: itemDTO.price,
                quantity: itemDTO.quantity,
                order: undefined,
            };
            return item;
        });
        const order: Order = {
            id: undefined,
            version: undefined,
            orderNumber: undefined,
            status: 'PROCESSING',
            totalAmount: undefined,
            items: orderedItems,
            customerId: orderDTO.customerId,
            createdAt: new Date(),
            updatedAt: new Date(),
        };

        return order;
    }

    #orderUpdateDtoToOrder(orderDTO: OrderUpdateDTO): Order {
        return {
            id: undefined,
            version: undefined,
            orderNumber: undefined,
            status: orderDTO.status,
            totalAmount: undefined,
            items: undefined,
            customerId: undefined,
            createdAt: undefined,
            updatedAt: new Date(),
        };
    }

    // #errorMsgCreateOrder(err: CreateError) {
    //     switch (err.type) {
    //         case 'IsbnExists': {
    //             return `Die ISBN ${err.isbn} existiert bereits`;
    //         }
    //         default: {
    //             return 'Unbekannter Fehler';
    //         }
    //     }
    // }

    // #errorMsgUpdateOrder(err: UpdateError) {
    //     switch (err.type) {
    //         case 'OrderNotExists': {
    //             return `Es gibt kein Order mit der ID ${err.id}`;
    //         }
    //         case 'VersionInvalid': {
    //             return `"${err.version}" ist keine gueltige Versionsnummer`;
    //         }
    //         case 'VersionOutdated': {
    //             return `Die Versionsnummer "${err.version}" ist nicht mehr aktuell`;
    //         }
    //         default: {
    //             return 'Unbekannter Fehler';
    //         }
    //     }
    // }
}
