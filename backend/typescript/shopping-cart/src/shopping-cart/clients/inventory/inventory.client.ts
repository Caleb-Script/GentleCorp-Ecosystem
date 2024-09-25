import { Module, Logger } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config'; // ConfigModule importieren
import { HttpModule, HttpService } from '@nestjs/axios';
import { InventoryRepository } from './inventory.repository';
import { AxiosRequestConfig } from 'axios';

@Module({
    imports: [HttpModule, ConfigModule], // ConfigModule hier hinzufügen
    providers: [
        {
            provide: 'INVENTORY_BASE_URL',
            useFactory: (configService: ConfigService) => {
                const inventoryDefaultPort = 8086;

                const inventorySchemaEnv = process.env.INVENTORY_SERVICE_SCHEMA;
                const inventoryHostEnv = process.env.INVENTORY_SERVICE_HOST;
                const inventoryPortEnv = process.env.INVENTORY_SERVICE_PORT;

                const schema = inventorySchemaEnv || 'http';
                const host = inventoryHostEnv || 'localhost';
                const port = inventoryPortEnv ? parseInt(inventoryPortEnv, 10) : inventoryDefaultPort;

                Logger.debug(`inventory: host=${host}, port=${port}`);
                return `${schema}://${host}:${port}`;
            },
            inject: [ConfigService],
        },
        {
            provide: InventoryRepository,
            useFactory: (httpService: HttpService, baseUrl: string) => {
                const axiosInstance = httpService.axiosRef;
                return new InventoryRepository(axiosInstance, baseUrl);
            },
            inject: [HttpService, 'INVENTORY_BASE_URL'],
        },
    ],
    exports: [InventoryRepository],
})
export class InventoryClientConfig { }