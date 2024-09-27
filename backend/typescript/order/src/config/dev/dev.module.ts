import { KeycloakModule } from '../../keycloak/keycloak.module';
import { order } from '../../order/model/entity/order.entity';
import { DbPopulateService } from './db-populate.service';
import { DevController } from './dev.controller';
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';

@Module({
  imports: [KeycloakModule, TypeOrmModule.forFeature([order])],
  controllers: [DevController],
  providers: [DbPopulateService],
  exports: [DbPopulateService],
})
export class DevModule {}
