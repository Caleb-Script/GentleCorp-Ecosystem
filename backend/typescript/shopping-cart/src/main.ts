import { AppModule } from './app.module';
import { corsOptions } from './config/cors.js';
import { nodeConfig } from './config/node';
import { NestFactory } from '@nestjs/core';

const { port } = nodeConfig;

async function bootstrap() {
  const customer = await NestFactory.create(AppModule);
  customer.enableCors(corsOptions);
  await customer.listen(port);
}

bootstrap();
