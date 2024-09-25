import { KeycloakModule } from '../../keycloak/keycloak.module';
import { ShoppingCart } from '../../shopping-cart/model/entity/shopping-cart.entity';
import { DbPopulateService } from './db-populate.service';
import { DevController } from './dev.controller';
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';

@Module({
  imports: [KeycloakModule, TypeOrmModule.forFeature([ShoppingCart])],
  controllers: [DevController],
  providers: [DbPopulateService],
  exports: [DbPopulateService],
})
export class DevModule {}
