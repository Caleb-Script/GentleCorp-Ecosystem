import { BannerService } from './banner.service';
import { ResponseTimeInterceptor } from './response-time.interceptor';
import { Global, Module } from '@nestjs/common';

/**
 * Das Modul besteht aus allgemeinen Services, z.B. MailService.
 * @packageDocumentation
 */

/**
 * Die dekorierte Modul-Klasse mit den Service-Klassen.
 */
@Global()
@Module({
  providers: [BannerService, ResponseTimeInterceptor],
  exports: [BannerService, ResponseTimeInterceptor],
})
export class LoggerModule {}
