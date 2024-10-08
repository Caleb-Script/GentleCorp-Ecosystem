import {
  type MiddlewareConsumer,
  Module,
  type NestModule,
} from '@nestjs/common';
import { AdminModule } from './admin/admin.module.js';
import { type ApolloDriverConfig } from '@nestjs/apollo';
import { OrderModule } from './buch/buch.module.js';
import { DevModule } from './config/dev/dev.module.js';
import { GraphQLModule } from '@nestjs/graphql';
import { KeycloakModule } from './security/keycloak/keycloak.module.js';
import { LoggerModule } from './logger/logger.module.js';
import { RequestLoggerMiddleware } from './logger/request-logger.middleware.js';
import { TypeOrmModule } from '@nestjs/typeorm';
import { graphQlModuleOptions } from './config/graphql.js';
import { typeOrmModuleOptions } from './config/typeormOptions.js';
import { AppController } from './app.controller';
import { AppService } from './app.service';

@Module({
  imports: [
    AdminModule,
    OrderModule,
    DevModule,
    GraphQLModule.forRoot<ApolloDriverConfig>(graphQlModuleOptions),
    LoggerModule,
    KeycloakModule,
    TypeOrmModule.forRoot(typeOrmModuleOptions),
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer
      .apply(RequestLoggerMiddleware)
      .forRoutes(
        'auth',
        'graphql',
      );
  }
}