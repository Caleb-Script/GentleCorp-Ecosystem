import { Injectable, Logger } from '@nestjs/common';
import { AxiosInstance } from 'axios';
import { Inventory } from './inventory.interface';

@Injectable()
export class InventoryRepository {
    private readonly logger = new Logger(InventoryRepository.name);
    private readonly baseUrl: string;

    constructor(private readonly axios: AxiosInstance, baseUrl: string) {
        this.baseUrl = baseUrl; // Speichern Sie die Basis-URL
    }

    async getById(id: string, version: string, authorization: string): Promise<Inventory> {
        this.logger.debug(`Fetching inventory with id=${id} and version=${version} token=${authorization}`);
        const url = `${this.baseUrl}/inventory/${id}`;
        this.logger.debug(`Requesting URL: ${url}`);

        const response = await this.axios.get<Inventory>(url, {
            headers: {
                'If-None-Match': version,
                Authorization: authorization,
            },
        });
        return response.data;
    }
}