import { HealthController } from './health.controller';
import { HttpModule } from '@nestjs/axios';
import { Module } from '@nestjs/common';
import { PrometheusController } from './prometheus.controller';
import { TerminusModule } from '@nestjs/terminus';

@Module({
    imports: [TerminusModule, HttpModule],
    controllers: [HealthController, PrometheusController],
})
export class AdminModule {}
