import { Module, Logger } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config'; // ConfigModule importieren
import { HttpModule, HttpService } from '@nestjs/axios'; // HttpService importieren
import { CustomerRepository } from './customer.repository';

@Module({
    imports: [HttpModule, ConfigModule], // ConfigModule hier hinzufügen
    providers: [
        {
            provide: 'CUSTOMER_BASE_URL',
            useFactory: (configService: ConfigService) => {
                const customerDefaultPort = 8080;

                const customerSchemaEnv = process.env.CUSTOMER_SERVICE_SCHEMA;
                const customerHostEnv = process.env.CUSTOMER_SERVICE_HOST;
                const customerPortEnv = process.env.CUSTOMER_SERVICE_PORT;

                const schema = customerSchemaEnv || 'http';
                const host = customerHostEnv || 'localhost';
                const port = customerPortEnv ? parseInt(customerPortEnv, 10) : customerDefaultPort;

                Logger.debug(`customer: host=${host}, port=${port}`);
                return `${schema}://${host}:${port}`;
            },
            inject: [ConfigService],
        },
        {
            provide: CustomerRepository,
            useFactory: (httpService: HttpService, baseUrl: string) => {
                const axiosInstance = httpService.axiosRef; // AxiosInstance aus HttpService verwenden
                return new CustomerRepository(axiosInstance, baseUrl); // CustomerRepository mit der AxiosInstance und baseUrl erstellen
            },
            inject: [HttpService, 'CUSTOMER_BASE_URL'], // HttpService hier injizieren
        },
    ],
    exports: [CustomerRepository],
})
export class CustomerClientConfig { }