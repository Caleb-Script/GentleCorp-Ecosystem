import {
    Column,
    CreateDateColumn,
    Entity,
    JoinColumn,
    ManyToOne,
    PrimaryGeneratedColumn,
    UpdateDateColumn,
    VersionColumn,
} from 'typeorm';
import { ShoppingCart } from './shopping-cart.entity.js';
import { DecimalTransformer } from './decimal-transformer.js';
import { Exclude } from 'class-transformer';

@Entity()
export class Item {
    @PrimaryGeneratedColumn()
    id: string | undefined;

    @VersionColumn()
    version: number | undefined;

    @Column()
    skuCode: string;

    @Exclude()
    quantity: number;

    @Column('decimal', {
        precision: 8,
        scale: 2,
        transformer: new DecimalTransformer(),
    })
    price: number;

    @Column()
    name: string;

    @CreateDateColumn({
        type: 'timestamp',
    })
    readonly created: Date | undefined;

    @UpdateDateColumn({
        type: `timestamp`,
    })
    readonly updated: Date | undefined;

    @ManyToOne(() => ShoppingCart, (shoppingCart) => shoppingCart.items)
    @JoinColumn({ name: 'shopping_cart_id' })
    customer: ShoppingCart | undefined;
}
