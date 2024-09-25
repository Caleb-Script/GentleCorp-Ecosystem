import { Module } from '@nestjs/common';
import { HttpModule } from '@nestjs/axios'; 
import { InventoryClientConfig } from './inventory.client';

@Module({
    imports: [HttpModule, InventoryClientConfig], 
    exports: [InventoryClientConfig], 
})
export class InventoryModule { }
