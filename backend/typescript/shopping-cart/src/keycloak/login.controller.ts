import {
    ApiConsumes,
    ApiOkResponse,
    ApiOperation,
    ApiProperty,
    ApiTags,
    ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import {
    Body,
    Controller,
    HttpCode,
    HttpStatus,
    Post,
    Res,
    UseInterceptors,
} from '@nestjs/common';
import { KeycloakService } from './keycloak.service.js';
import { Public } from 'nest-keycloak-connect';
import { Response } from 'express';
import { paths } from '../config/paths.js';
import { ResponseTimeInterceptor } from '../logger/response-time.interceptor.js';
import { getLogger } from '../logger/logger.js';



/** Entity-Klasse für Login-Daten. */
export class Login {
    /** Benutzername */
    // https://docs.nestjs.com/openapi/types-and-parameters
    @ApiProperty({ example: 'admin', type: String })
    username: string | undefined;

    /** Passwort */
    @ApiProperty({ example: 'p', type: String })
    password: string | undefined;
}

/** Entity-Klasse für Refresh-Token. */
export class Refresh {
    /** Refresh Token */
    @ApiProperty({ example: 'alg.payload.signature', type: String })
    refresh_token: string | undefined; // eslint-disable-line @typescript-eslint/naming-convention, camelcase
}

/**
 * Die Controller-Klasse für die Authentifizierung.
 */
@Controller(paths.auth)
@UseInterceptors(ResponseTimeInterceptor)
@ApiTags('Authentifizierung und Autorisierung')
export class LoginController {
    readonly #keycloakService: KeycloakService;

    readonly #logger = getLogger(LoginController.name);

    constructor(keycloakService: KeycloakService) {
        this.#keycloakService = keycloakService;
    }

    @Post(paths.login)
    @Public()
    @HttpCode(HttpStatus.OK)
    @ApiConsumes('application/x-www-form-urlencoded', 'application/json')
    @ApiOperation({ summary: 'Login mit Benutzername und Passwort' })
    @ApiOkResponse({ description: 'Erfolgreich eingeloggt.' })
    @ApiUnauthorizedResponse({
        description: 'Benutzername oder Passwort sind falsch.',
    })
    async login(@Body() { username, password }: Login, @Res() res: Response) {
        this.#logger.debug('login: username=%s', username);

        const result = await this.#keycloakService.login({
            username,
            password,
        });
        if (result === undefined) {
            return res.sendStatus(HttpStatus.UNAUTHORIZED);
        }

        return res.json(result).send();
    }

    @Post(paths.refresh)
    @Public()
    @HttpCode(HttpStatus.OK)
    @ApiConsumes('application/x-www-form-urlencoded', 'application/json')
    @ApiOperation({ summary: 'Refresh für vorhandenen Token' })
    @ApiOkResponse({ description: 'Erfolgreich aktualisiert.' })
    @ApiUnauthorizedResponse({
        description: 'Ungültiger Token.',
    })
    async refresh(@Body() body: Refresh, @Res() res: Response) {
        // eslint-disable-next-line camelcase, @typescript-eslint/naming-convention
        const { refresh_token } = body;
        this.#logger.debug('refresh: refresh_token=%s', refresh_token);

        const result = await this.#keycloakService.refresh(refresh_token);
        if (result === undefined) {
            return res.sendStatus(HttpStatus.UNAUTHORIZED);
        }

        return res.json(result).send();
    }
}
