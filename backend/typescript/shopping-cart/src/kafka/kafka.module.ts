import { ConsumerService } from './Consumer.service';
import { ProducerService } from './Producer.service';
import { Module } from '@nestjs/common';

@Module({
  providers: [ProducerService, ConsumerService],
  exports: [ProducerService, ConsumerService],
})
export class KafkaModule {}
