import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { DevModule } from './config/dev/dev.module';
import { LoggerModule } from './logger/logger.module';
import { KeycloakModule } from './keycloak/keycloak.module';
import { TypeOrmModule } from '@nestjs/typeorm';
import { typeOrmModuleOptions } from './config/typeormOptions';
import { AdminModule } from './admin/admin.module';
import { ShoppingCartModule } from './shopping-cart/shopping-cart.module';
import { KafkaModule } from './kafka/kafka.module';
import { ClientModule } from './shopping-cart/clients/client.module';

@Module({
  imports: [
    AdminModule,
    ClientModule,
    KafkaModule,
    ShoppingCartModule,
    DevModule,
    LoggerModule,
    KeycloakModule,
    TypeOrmModule.forRoot(typeOrmModuleOptions),
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
