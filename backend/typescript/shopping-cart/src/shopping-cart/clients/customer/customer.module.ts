import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios'; 
import { CustomerClientConfig } from './customer.client';

@Module({
    imports: [HttpModule, CustomerClientConfig], 
    exports: [CustomerClientConfig], // Exportieren Sie nur CustomerClientConfig
})
export class CustomerModule { }
