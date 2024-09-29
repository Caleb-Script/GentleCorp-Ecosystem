import {
    Column,
    CreateDateColumn,
    Entity,
    OneToMany,
    PrimaryGeneratedColumn,
    UpdateDateColumn,
    VersionColumn,
} from 'typeorm';
import { Item } from './item.entity';
import { ApiProperty } from '@nestjs/swagger';
import { DecimalTransformer } from './decimal-transformer';

export type OrderStatus = 'PAID' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'UNPAID';

@Entity()
export class Order {
    @PrimaryGeneratedColumn('uuid')
    @ApiProperty({ example: '123e4567-e89b-12d3-a456-426614174000' })
    id: string;

    @VersionColumn()
    @ApiProperty({ example: 1 })
    version: number;

    @Column({ unique: true })
    @ApiProperty({ example: 'ORD-2023-001' })
    orderNumber: string;

    @Column({
        type: 'enum',
        enum: ['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'UNPAID'],
        default: 'PROCESSING'
    })
    @ApiProperty({ enum: ['PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'UNPAID'] })
    status: OrderStatus;

    @Column()
    @ApiProperty({ example: '00000000-0000-0000-0000-000000000000' })
    customerId: string;

    @Column('decimal', {
        precision: 8,
        scale: 2,
        transformer: new DecimalTransformer(),
    })
    @ApiProperty({ example: 19.99 })
    totalAmount: number;

    @OneToMany(() => Item, (item) => item.order, {
        cascade: ['insert', 'remove'],
    })
    items: Item[];

    @CreateDateColumn({ type: 'timestamp' })
    @ApiProperty({ example: '2023-01-01T00:00:00Z' })
    createdAt: Date;

    @UpdateDateColumn({ type: 'timestamp' })
    @ApiProperty({ example: '2023-01-01T00:00:00Z' })
    updatedAt: Date;

    toString(): string {
        return JSON.stringify({
            id: this.id,
            version: this.version,
            orderNumber: this.orderNumber,
            status: this.status,
            customerId: this.customerId,
            totalAmount: this.totalAmount,
            createdAt: this.createdAt,
            updatedAt: this.updatedAt,
        });
    }
}
