import { corsOptions } from './config/cors.js';
import { nodeConfig } from './config/node';
import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';

const { port } = nodeConfig;

async function bootstrap() {
    const shoppingCart = await NestFactory.create(AppModule);
    shoppingCart.enableCors(corsOptions);
    await shoppingCart.listen(port);
}

bootstrap();
