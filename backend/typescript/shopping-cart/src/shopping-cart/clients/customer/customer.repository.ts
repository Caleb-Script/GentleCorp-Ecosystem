import { Injectable, Logger } from '@nestjs/common';
import { AxiosInstance } from 'axios';
import { Customer } from './customer.interface';

@Injectable()
export class CustomerRepository {
    private readonly logger = new Logger(CustomerRepository.name);
    private readonly baseUrl: string;

    constructor(private readonly axios: AxiosInstance, baseUrl: string) {
        this.baseUrl = baseUrl; // Speichern Sie die Basis-URL
    }

    async getById(id: string, version: string, authorization: string): Promise<Customer> {
        this.logger.debug(`Fetching customer with id=${id} and version=${version} token=${authorization}`);
        const url = `${this.baseUrl}/customer/${id}`; // Verwenden Sie die Basis-URL
        this.logger.debug(`Requesting URL: ${url}`);

        const response = await this.axios.get<Customer>(url, {
            headers: {
                'If-None-Match': version,
                Authorization: authorization,
            },
        });
        return response.data;
    }
}