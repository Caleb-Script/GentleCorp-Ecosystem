import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { entities } from './model/entity/entities.js';
import { ShoppingCartReadService } from './service/shopping-cart-read.service.js';
import { ShoppingCartGetController } from './controller/shopping-cart-get.controller.js';
import { KeycloakModule } from '../keycloak/keycloak.module.js';
import { ShoppingCartQueryBuilder } from './service/query-builder.js';
import { ShoppingCartWriteController } from './controller/shopping-cart-write.controller.js';
import { ShoppingCartWriteService } from './service/shopping-cart-write.service.js';
import { KafkaModule } from '../kafka/kafka.module.js';

@Module({
  imports: [KafkaModule, KeycloakModule, TypeOrmModule.forFeature(entities)],
    controllers: [
        ShoppingCartGetController,
        ShoppingCartWriteController
    ],
    // Provider sind z.B. Service-Klassen fuer DI
    providers: [
        ShoppingCartReadService,
        ShoppingCartWriteService,
        ShoppingCartQueryBuilder,
    ],
    // Export der Provider fuer DI in anderen Modulen
    exports: [
        ShoppingCartReadService,
        ShoppingCartWriteService
    ],
})
export class ShoppingCartModule {}
