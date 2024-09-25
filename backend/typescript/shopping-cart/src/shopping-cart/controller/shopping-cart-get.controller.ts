import {
    Controller,
    Get,
    Headers,
    HttpStatus,
    Param,
    Query,
    Req,
    Res,
    UseInterceptors,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { Public, Roles } from 'nest-keycloak-connect';
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor';
import { getLogger } from '../../logger/logger';
import { paths } from '../../config/paths';
import { type ShoppingCart } from '../model/entity/shopping-cart.entity';
import { type SearchCriteria } from '../model/searchCriteria';
import { FindParams, ShoppingCartReadService } from '../service/shopping-cart-read.service';
import { getBaseUri } from './getBaseUri';
import { Item } from '../model/entity/item.entity';


/** href-Link für HATEOAS */
export interface Link {
    readonly href: string;
}

/** Links für HATEOAS */
export interface Links {
    readonly self: Link;
    readonly list?: Link;
    readonly add?: Link;
    readonly update?: Link;
    readonly remove?: Link;
}

/** ShoppingCart-Objekt mit HATEOAS-Links */
export type ShoppingCartModel = Omit<ShoppingCart, 'id' | 'version' | 'created' | 'updated' | 'cartItems'> & ItemModels & { _links: Links; };

/** ShoppingCart-Objekte mit HATEOAS-Links in einem JSON-Array. */
export interface ShoppingCartModels {
    _embedded: {
        shoppingCarts: ShoppingCartModel[];
    };
}

export type ItemModel = Omit<Item, 'id' | 'version' | 'created' | 'updated' | 'shoppingCart'>

export interface ItemModels {
    cartItems: ItemModel[];
}

export class ShopppingCartQuery {
    isComplete: string;
    totalAmount: number;
}

const APPLICATION_HAL_JSON = 'application/hal+json';

@Controller(paths.shoppinCart)
@UseInterceptors(ResponseTimeInterceptor)
export class ShoppingCartGetController {
    readonly #service: ShoppingCartReadService;
    readonly #logger = getLogger(ShoppingCartGetController.name);

    constructor(service: ShoppingCartReadService) {
        this.#service = service;
    }

    @Get(':id')
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user', 'gentlecorp-customer'] })
    async getById(
        @Param('id') id: string,
        @Req() req: Request,
        @Headers('If-None-Match') version: string | undefined,
        @Headers('Authorization') authorization: string | undefined,
        @Res() res: Response,
    ): Promise<Response<ShoppingCartModel | undefined>> {
        this.#logger.debug('getById: idStr=%s, version=%s', id, version);

        if (req.accepts([APPLICATION_HAL_JSON, 'json', 'html']) === false) {
            this.#logger.debug('getById: accepted=%o', req.accepted);
            return res.sendStatus(HttpStatus.NOT_ACCEPTABLE);
        }

        if (version == undefined) {
            this.#logger.error('getById: version is undefined');
            return res.sendStatus(HttpStatus.PRECONDITION_REQUIRED)
        }

        const shoppingCart = await this.#service.findById({ id, withItems: true, authorization });
        if (this.#logger.isLevelEnabled('debug')) {
            this.#logger.debug('getById(): shoppingCart=%s', shoppingCart.toString());
        }

        // ETags
        const versionDb = shoppingCart.version;
        if (version === `"${versionDb}"`) {
            this.#logger.debug('getById: NOT_MODIFIED');
            return res.sendStatus(HttpStatus.NOT_MODIFIED);
        }
        this.#logger.debug('getById: versionDb=%s', versionDb);
        res.header('ETag', `"${versionDb}"`);

        // HATEOAS mit Atom Links und HAL
        const shoppingCartModel = this.#toModel(shoppingCart, req);
        this.#logger.debug('getById: shoppingCartModel=%o', shoppingCartModel);
        return res.contentType(APPLICATION_HAL_JSON).json(shoppingCartModel);
    }

    @Get()
    @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user'] })
    async get(
        @Query() query: ShopppingCartQuery,
        @Req() req: Request,
        @Res() res: Response,
    ): Promise<Response<ShoppingCartModels | undefined>> {
        this.#logger.debug('get: query=%o', query);

        // Manuelle Umwandlung in FindParams
        const findParams: FindParams = {
            searchCriteria: {
                totalAmount: query.totalAmount ? Number(query.totalAmount) : undefined,
                isComplete: query.isComplete === 'true' ? true : false
            }
        };

        const shoppingCarts = await this.#service.find(findParams); // Verwenden Sie findParams hier
        this.#logger.debug('get: %o', shoppingCarts);

        const shoppingCartsModel = shoppingCarts.map((shoppingCart, index) =>
            this.#toModel(shoppingCart, req, false)
        );
        this.#logger.debug('get: shoppingCartsModel=%o', shoppingCartsModel);

        const result: ShoppingCartModels = { _embedded: { shoppingCarts: shoppingCartsModel } };
        return res.contentType(APPLICATION_HAL_JSON).json(result).send();
    }

    #toModel(shoppingCart: ShoppingCart, req: Request, all = true) {
        const baseUri = getBaseUri(req);
        const { id } = shoppingCart;
        const links = all
            ? {
                  self: { href: `${baseUri}/${id}` },
                  list: { href: `${baseUri}` },
                  add: { href: `${baseUri}` },
                  update: { href: `${baseUri}/${id}` },
                  remove: { href: `${baseUri}/${id}` },
              }
            : { self: { href: `${baseUri}/${id}` } };

        const shoppingCartModel: ShoppingCartModel = {
            totalAmount: shoppingCart.totalAmount,
            customerId: shoppingCart.customerId,
            customerUsername: shoppingCart.customerUsername,
            isComplete: shoppingCart.isComplete,
            cartItems: shoppingCart.cartItems?.map((item, index) => ({
                ...this.#toItemModel(item)
            })),
            _links: links,
        };

        return shoppingCartModel;
    }

    #toItemModel(item: Item) {
        // Implementieren Sie die Logik zur Umwandlung des Item-Objekts in das gewünschte Format
        return {
            id: item.id,
            quantity: item.quantity,
            name: item.name,
            price: item.price,
            skuCode: item.skuCode,
            inventoryId: item.inventoryId
        };
    }
}
