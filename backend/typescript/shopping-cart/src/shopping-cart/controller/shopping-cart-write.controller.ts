import { AuthGuard, Public, Roles } from 'nest-keycloak-connect';
import {
  Body,
  ConflictException,
  Controller,
  Delete,
  Headers,
  HttpCode,
  HttpStatus,
  Param,
  Post,
  Put,
  Req,
  Res,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';

import { Request, Response } from 'express';
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor';
import { getBaseUri } from './getBaseUri';
import { getLogger } from '../../logger/logger';
import { paths } from '../../config/paths';
import { ShoppingCartDTO, ShoppingCartDTOOhneRef } from '../model/dto/shopping-cart.dto';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { ShoppingCartWriteService } from '../service/shopping-cart-write.service';
import { ConsumerService } from '../../kafka/Consumer.service';
import { ItemDTO } from '../model/dto/item.dto';
import { Item } from '../model/entity/item.entity';
import { ShoppingCartReadService } from '../service/shopping-cart-read.service';



const MSG_FORBIDDEN = 'Kein Token mit ausreichender Berechtigung vorhanden';
/**
 * Die Controller-Klasse für die Verwaltung von Bücher.
 */
@Controller(paths.shoppinCart)
@UseGuards(AuthGuard)
@UseInterceptors(ResponseTimeInterceptor)
export class ShoppingCartWriteController {
  readonly #writeService: ShoppingCartWriteService;
  readonly #readService: ShoppingCartReadService;

  readonly #logger = getLogger(ShoppingCartWriteController.name);

  constructor(
    writeService: ShoppingCartWriteService,
    readService: ShoppingCartReadService,
    private readonly consumerService: ConsumerService,
  ) {

    this.#writeService = writeService;
    this.#readService = readService;
  }

  @Post()
  @Roles({ roles: ['gentlecorp-admin'] })
  async post(
    @Body() shoppingCartDTO: ShoppingCartDTO,
    @Req() req: Request,
    @Headers('Authorization') authorization: string | undefined,
    @Res() res: Response,
  ): Promise<Response> {
    this.#logger.debug('post: shoppingCartDTO=%o', shoppingCartDTO);
    await this.#readService.getCustomer({ customerId: shoppingCartDTO.customerId, token: authorization });
    const id = await this.#writeService.create(shoppingCartDTO);

    const location = `${getBaseUri(req)}/${id}`;
    this.#logger.debug('post: location=%s', location);
    return res.location(location).send();
  }

  @Put(':id/add')
  @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user', 'gentlecorp-customer'] })
  async addItem(
    @Param('id') cartId: string,
    @Req() req: Request,
    @Headers('If-Match') cartVersionStr: string | undefined,
    @Body() itemDTO: ItemDTO,
    @Headers('Authorization') token: string | undefined,
    @Res() res: Response,
  ): Promise<Response> {
  
    if (!cartVersionStr) {
      const msg = 'Header "If-Match" fehlt';
      this.#logger.debug('addItem: %s', msg);
      return res.status(HttpStatus.PRECONDITION_REQUIRED).json({
        statusCode: HttpStatus.PRECONDITION_REQUIRED,
        message: 'Header "If-Match" fehlt'
      });
    }

    const cartVersion: number = Number.parseInt(cartVersionStr.slice(1, -1), 10);
    this.#logger.debug('addItem: shoppingCartId=%s, body=%o', cartId, itemDTO);
    const { id, version } = await this.#writeService.addItemToCart({cartId, itemDTO, cartVersion, token});
    const location = `${getBaseUri(req)}/${id}`;
    this.#logger.debug('addItem: location=%s', location);
    this.#logger.debug('addItem: version=%d', version);
    
    return res.
      header('ETag', `"${version}"`)
      .location(location)
      .status(HttpStatus.CREATED)
      .send();
  }

  @Put(':id/remove')
  @Roles({ roles: ['gentlecorp-admin', 'gentlecorp-user', 'gentlecorp-customer'] })
  async removeItem(
    @Param('id') cartId: string,
    @Body() itemDTO: ItemDTO,
    @Headers('If-Match') shoppingCartVersion: string | undefined,
    @Headers('Authorization') token: string | undefined,
    @Res() res: Response,
  ): Promise<Response> {

    if (!shoppingCartVersion) {
      const msg = 'Header "If-Match" fehlt';
      this.#logger.debug('removeItem: %s', msg);
      return res.status(HttpStatus.PRECONDITION_REQUIRED).json({
        statusCode: HttpStatus.PRECONDITION_REQUIRED,
        message: 'Header "If-Match" fehlt'
      });
    }

    const cartVersion: number = Number.parseInt(shoppingCartVersion.slice(1, -1), 10);
    this.#logger.debug('removeItem: shoppingCartId=%s, body=%o', cartId, itemDTO);
    const { version } = await this.#writeService.removeItemFromCart({cartId, cartVersion, itemDTO, token});
    this.#logger.debug('removeItem: version=%d', version);
    
    return res
      .status(HttpStatus.NO_CONTENT)
      .header('ETag', `"${version}"`)
      .send();
  }

  @Delete(':id')
  @Roles({ roles: ['gentlecorp-admin'] })
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(
    @Param('id') id: string,
    @Headers('Authorization') authorization: string | undefined,
    @Headers('If-Match') cartVersionStr: string | undefined,
    @Res() res: Response,
  ) {
    if (!cartVersionStr) {
      const msg = 'Header "If-Match" fehlt';
      this.#logger.debug('removeItem: %s', msg);
      return res.status(HttpStatus.PRECONDITION_REQUIRED).json({
        statusCode: HttpStatus.PRECONDITION_REQUIRED,
        message: 'Header "If-Match" fehlt'
      });
    }
    const cartVersion: number = Number.parseInt(cartVersionStr.slice(1, -1), 10);
    const shoppingCart = await this.#readService.findById({id, withItems: true, authorization})
    if (shoppingCart.version !== cartVersion) {
      throw new ConflictException('Shopping cart version mismatch.');
    }

    this.#logger.debug('delete: id=%s', id);
    await this.#writeService.delete(id, authorization);
    
    return res
      .status(HttpStatus.NO_CONTENT)
      .send();
  }

  

  #shoppingCartDTOOhneRefToShoppingCart(shoppingCartDTO: ShoppingCartDTOOhneRef): ShoppingCart {
    return {
      id: undefined,
      version: undefined,
      customerId: shoppingCartDTO.customerId,
      cartItems: undefined,
      created: undefined,
      updated: new Date(),
      totalAmount: undefined,
      isComplete: undefined,
      customerUsername: undefined,
    };
  }

}
