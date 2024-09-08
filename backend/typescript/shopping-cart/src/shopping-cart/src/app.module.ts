import { Module } from '@nestjs/common';
import { ShoppingCartModule } from './shopping-cart/shopping-cart.module';

@Module({
    imports: [ShoppingCartModule],
})
export class AppModule {}
