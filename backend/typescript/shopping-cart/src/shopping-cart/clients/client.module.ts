import { Module } from '@nestjs/common';
import { InventoryModule } from './inventory/inventory.module';
import { CustomerModule } from './customer/customer.module';

@Module({
    imports: [InventoryModule, CustomerModule],
    exports: [InventoryModule, CustomerModule]
})
export class ClientModule { }