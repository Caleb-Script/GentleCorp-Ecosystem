import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { entities } from './model/entity/entities';
import { ShoppingCartReadService } from './service/shopping-cart-read.service';
import { ShoppingCartGetController } from './controller/shopping-cart-get.controller';
import { KeycloakModule } from '../keycloak/keycloak.module';
import { ShoppingCartQueryBuilder } from './service/query-builder';
import { ShoppingCartWriteController } from './controller/shopping-cart-write.controller';
import { ShoppingCartWriteService } from './service/shopping-cart-write.service';
import { KafkaModule } from '../kafka/kafka.module';
import { CustomerModule } from './clients/customer/customer.module';
import { ClientModule } from './clients/client.module';

@Module({
    imports: [KafkaModule, KeycloakModule, TypeOrmModule.forFeature(entities), ClientModule],
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
