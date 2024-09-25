import { ResponseTimeInterceptor } from '../../logger/response-time.interceptor';
import { DbPopulateService } from './db-populate.service';
import {
  Controller,
  HttpStatus,
  Post,
  Res,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import {
  ApiBearerAuth,
  ApiForbiddenResponse,
  ApiOkResponse,
  ApiOperation,
  ApiTags,
} from '@nestjs/swagger';
import { Response } from 'express';
import { AuthGuard, Roles } from 'nest-keycloak-connect';

/**
 * Die Controller-Klasse für die Entwicklung, z.B. Neuladen der DB.
 */
@Controller('dev')
@UseGuards(AuthGuard)
@Roles({ roles: ['admin'] })
@UseInterceptors(ResponseTimeInterceptor)
@ApiTags('Dev')
export class DevController {
  readonly #service: DbPopulateService;

  constructor(service: DbPopulateService) {
    this.#service = service;
  }

  @Post('db_populate')
  @ApiOperation({ summary: 'DB neu laden' })
  @ApiBearerAuth()
  @ApiOkResponse({ description: 'Die DB wurde neu geladen' })
  @ApiForbiddenResponse({
    description: 'Kein Token mit ausreichender Berechtigung vorhanden',
  })
  async dbPopulate(@Res() res: Response): Promise<Response> {
    await this.#service.populateTestdaten();
    const success = {
      // eslint-disable-next-line @typescript-eslint/naming-convention, camelcase
      db_populate: 'success',
    };
    return res.status(HttpStatus.OK).json(success);
  }
}
