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
import { Public } from 'nest-keycloak-connect';
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor.js';;
import { getLogger } from '../../logger/logger.js';
import { paths } from '../../config/paths.js';
import { type ShoppingCart } from '../model/entity/shopping-cart.entity.js';
import { type SearchCriteria } from '../model/searchCriteria.js';
import { ShoppingCartReadService } from '../service/shopping-cart-read.service.js';
import { getBaseUri } from './getBaseUri.js';

/** href-Link für HATEOAS */
export interface Link {
    /** href-Link für HATEOAS-Links */
    readonly href: string;
}

/** Links für HATEOAS */
export interface Links {
    /** self-Link */
    readonly self: Link;
    /** Optionaler Linke für list */
    readonly list?: Link;
    /** Optionaler Linke für add */
    readonly add?: Link;
    /** Optionaler Linke für update */
    readonly update?: Link;
    /** Optionaler Linke für remove */
    readonly remove?: Link;
}


/** ShoppingCart-Objekt mit HATEOAS-Links */
export type ShoppingCartModel = Omit<ShoppingCart, 'shoppingCartId' | 'version' | 'created' | 'updated' | 'cartItems'> & {_links: Links;};

/** ShoppingCart-Objekte mit HATEOAS-Links in einem JSON-Array. */
export interface ShoppingCartsModel {
    _embedded: {
        shoppingCarts: ShoppingCartModel[];
    };
}

export class ShopppingCartQuery implements SearchCriteria {

}

const APPLICATION_HAL_JSON = 'application/hal+json';

@Controller(paths.shoppinCart)
@UseInterceptors(ResponseTimeInterceptor)
// @ApiBearerAuth()
export class ShoppingCartGetController {
    readonly #service: ShoppingCartReadService;

    readonly #logger = getLogger(ShoppingCartGetController.name);
    constructor(service: ShoppingCartReadService) {
        this.#service = service;
    }

    @Get(':shoppingCartId')
    @Public()
    async getById(
        @Param('shoppingCartId') shoppingCartId: string,
        @Req() req: Request,
        @Headers('If-None-Match') version: string | undefined,
        @Res() res: Response,
    ): Promise<Response<ShoppingCartModel | undefined>> {
        this.#logger.debug('getById: idStr=%s, version=%s', shoppingCartId, version);

        if (req.accepts([APPLICATION_HAL_JSON, 'json', 'html']) === false) {
            this.#logger.debug('getById: accepted=%o', req.accepted);
            return res.sendStatus(HttpStatus.NOT_ACCEPTABLE);
        }

        const shoppingCart = await this.#service.findById({ shoppingCartId, withItems: false });
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

        // HATEOAS mit Atom Links und HAL (= Hypertext Application Language)
        const buchModel = this.#toModel(shoppingCart, req);
        this.#logger.debug('getById: buchModel=%o', buchModel);
        return res.contentType(APPLICATION_HAL_JSON).json(buchModel);
    }

    @Get()
    @Public()
    async get(
        @Query() query: ShopppingCartQuery,
        @Req() req: Request,
        @Res() res: Response,
    ): Promise<Response<ShoppingCartsModel | undefined>> {
        this.#logger.debug('get: query=%o', query);

        if (req.accepts([APPLICATION_HAL_JSON, 'json', 'html']) === false) {
            this.#logger.debug('get: accepted=%o', req.accepted);
            return res.sendStatus(HttpStatus.NOT_ACCEPTABLE);
        }

        const shoppingCarts = await this.#service.find(query);
        this.#logger.debug('get: %o', shoppingCarts);

        // HATEOAS: Atom Links je ShoppingCart
        const buecherModel = shoppingCarts.map((shoppingCart) =>
            this.#toModel(shoppingCart, req, false),
        );
        this.#logger.debug('get: buecherModel=%o', buecherModel);

        const result: ShoppingCartsModel = { _embedded: { shoppingCarts: buecherModel } };
        return res.contentType(APPLICATION_HAL_JSON).json(result).send();
    }

    #toModel(shoppingCart: ShoppingCart, req: Request, all = true) {
        const baseUri = getBaseUri(req);
        this.#logger.debug('#toModel: baseUri=%s', baseUri);
        const { shoppingCartId } = shoppingCart;
        const links = all
            ? {
                  self: { href: `${baseUri}/${shoppingCartId}` },
                  list: { href: `${baseUri}` },
                  add: { href: `${baseUri}` },
                  update: { href: `${baseUri}/${shoppingCartId}` },
                  remove: { href: `${baseUri}/${shoppingCartId}` },
              }
            : { self: { href: `${baseUri}/${shoppingCartId}` } };

        this.#logger.debug('#toModel: shoppingCart=%o, links=%o', shoppingCart, links);

        const shoppingCartModel: ShoppingCartModel = {
            totalAmount: shoppingCart.totalAmount,
            customerId: shoppingCart.customerId,
            customerUsername: shoppingCart.customerUsername,
            isComplete: shoppingCart.isComplete,
            // items: shoppingCart.items?.map((item) => this.#toItemModel(item, req)),
            _links: links,
        };

        return shoppingCartModel;
    }
}
