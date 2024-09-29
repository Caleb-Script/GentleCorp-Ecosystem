import {
    Column,
    Entity,
    JoinColumn,
    ManyToOne,
    PrimaryGeneratedColumn,
} from 'typeorm';
import { Order } from './order.entity.js';
import { ApiProperty } from '@nestjs/swagger';
import { DecimalTransformer } from '../transformer/decimal-transformer';

@Entity()
export class Item {
    @PrimaryGeneratedColumn('uuid')
    @ApiProperty({ example: '123e4567-e89b-12d3-a456-426614174000' })
    id: string;

    @Column()
    @ApiProperty({ example: 'SKU-12345' })
    skuCode: string;

    @Column('decimal', {
        precision: 8,
        scale: 2,
        transformer: new DecimalTransformer(),
    })
    @ApiProperty({ example: 19.99 })
    price: number;

    @Column()
    @ApiProperty({ example: 2 })
    quantity: number;

    @ManyToOne(() => Order, (order) => order.items)
    @JoinColumn({ name: 'order_id' })
    @ApiProperty({ type: () => Order })
    order: Order;

    toString(): string {
        return JSON.stringify({
            id: this.id,
            skuCode: this.skuCode,
            price: this.price,
            quantity: this.quantity,
            orderId: this.order?.id,
        });
    }
}
