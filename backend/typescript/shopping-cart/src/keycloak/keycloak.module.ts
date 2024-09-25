// eslint-disable-next-line max-classes-per-file
import {
    AuthGuard,
    KeycloakConnectModule,
    RoleGuard,
} from 'nest-keycloak-connect';
import { APP_GUARD } from '@nestjs/core';
import { KeycloakService } from './keycloak.service';
import { LoginController } from './login.controller';
import { Module } from '@nestjs/common';

@Module({
    providers: [KeycloakService],
    exports: [KeycloakService],
})
class ConfigModule {}

@Module({
    imports: [
        KeycloakConnectModule.registerAsync({
            useExisting: KeycloakService,
            imports: [ConfigModule],
        }),
    ],
    controllers: [LoginController],
    providers: [
        KeycloakService,
        {
            // fuer @UseGuards(AuthGuard)
            provide: APP_GUARD,
            useClass: AuthGuard,
        },
        {
            // fuer @Roles({ roles: ['admin'] }) einschl. @Public() und @AllowAnyRole()
            provide: APP_GUARD,
            useClass: RoleGuard,
        },
    ],
    exports: [KeycloakConnectModule, KeycloakService],
})
export class KeycloakModule {}
