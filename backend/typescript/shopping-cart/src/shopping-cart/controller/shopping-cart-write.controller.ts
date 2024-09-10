import { AuthGuard, Public, Roles } from 'nest-keycloak-connect';
import {
  Body,
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
import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor.js';
import { getBaseUri } from './getBaseUri.js';
import { getLogger } from '../../logger/logger.js';
import { paths } from '../../config/paths.js';
import { ShoppingCartDTO, ShoppingCartDTOOhneRef } from '../model/dto/shopping-cart.dto.js';
import { ShoppingCart } from '../model/entity/shopping-cart.entity.js';
import { ShoppingCartWriteService } from '../service/shopping-cart-write.service.js';
import { ConsumerService } from '../../kafka/Consumer.service.js';



const MSG_FORBIDDEN = 'Kein Token mit ausreichender Berechtigung vorhanden';
/**
 * Die Controller-Klasse für die Verwaltung von Bücher.
 */
@Controller(paths.shoppinCart)
@UseGuards(AuthGuard)
@UseInterceptors(ResponseTimeInterceptor)
export class ShoppingCartWriteController {
  readonly #service: ShoppingCartWriteService;

  readonly #logger = getLogger(ShoppingCartWriteController.name);

  constructor(
    service: ShoppingCartWriteService,
    private readonly consumerService: ConsumerService,
  ) {

    this.#service = service;
  }

  async onModuleInit() {
    await this.consumerService.consume(
      {
        topics: ['create-shopping-cart'],
      },
      {
        eachMessage: async ({ topic, partition, message }) => {
          try {
            const messageValue = message.value.toString();
            console.log({
              topic,
              partition,
              value: messageValue,
            });
            let parsedMessage;
            parsedMessage = JSON.parse(messageValue);
            const shoppingCartDTO: ShoppingCartDTO = parsedMessage;
            const shoppingCart = this.#shoppingCartDTOToShoppingCart(shoppingCartDTO);
            await this.#service.create(shoppingCart);
          } catch (err) {
            console.error(err);
          }
        },
      }
  );
  }

  @Post()
  // @Roles({ roles: ['admin', 'user'] })
  @Public()
  async post(
    @Body() shoppingCartDTO: ShoppingCartDTO,
    @Req() req: Request,
    @Res() res: Response,
  ): Promise<Response> {
    this.#logger.debug('post: shoppingCartDTO=%o', shoppingCartDTO);

    const shoppingCart = this.#shoppingCartDTOToShoppingCart(shoppingCartDTO);
    const id = await this.#service.create(shoppingCart);

    const location = `${getBaseUri(req)}/${id}`;
    this.#logger.debug('post: location=%s', location);
    return res.location(location).send();
  }

  @Put(':id')
  // @Roles({ roles: ['admin', 'user'] })
  @Public()
  @HttpCode(HttpStatus.NO_CONTENT)
  async put(
    @Body() shoppingCartDTO: ShoppingCartDTOOhneRef,
    @Param('id') shoppingCartId: string,
    @Headers('If-Match') version: string | undefined,
    @Res() res: Response,
  ): Promise<Response> {
    this.#logger.debug(
      'put: id=%s, shoppingCartDTO=%o, version=%s',
      shoppingCartId,
      shoppingCartDTO,
      version,
    );

    if (version === undefined) {
      const msg = 'Header "If-Match" fehlt';
      this.#logger.debug('put: msg=%s', msg);
      return res
        .status(HttpStatus.PRECONDITION_REQUIRED)
        .set('Content-Type', 'application/json')
        .send(msg);
    }

    const shoppingCart = this.#shoppingCartDTOOhneRefToShoppingCart(shoppingCartDTO);
    const neueVersion = await this.#service.update({ shoppingCartId, shoppingCart, version });
    this.#logger.debug('put: version=%d', neueVersion);
    return res.header('ETag', `"${neueVersion}"`).send();
  }

  @Delete(':id')
  // @Roles({ roles: ['admin'] })
  @Public()
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(@Param('id') id: string) {
    this.#logger.debug('delete: id=%s', id);
    await this.#service.delete(id);
  }

  #shoppingCartDTOToShoppingCart(shoppingCartDTO: ShoppingCartDTO): ShoppingCart {
    const shoppingCart = {
      shoppingCartId: undefined,
      version: undefined,
      totalAmount: undefined,
      isComplete: undefined,
      customerUsername: undefined,
      customerId: shoppingCartDTO.customerId,
      cartItems: undefined,
      created: new Date(),
      updated: new Date(),
    };

    return shoppingCart;
  }

  #shoppingCartDTOOhneRefToShoppingCart(shoppingCartDTO: ShoppingCartDTOOhneRef): ShoppingCart {
    return {
      shoppingCartId: undefined,
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
