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
import { dbType } from '../../../config/db';
import { Exclude } from 'class-transformer';

@Entity()
export class ShoppingCart {
    @PrimaryGeneratedColumn(`uuid`)
    shoppingCartId: string | undefined;

    @VersionColumn()
    version: number | undefined;

    @Exclude()
    totalAmount: number | undefined;

    @Column()
    customerId: string | undefined;

    @Exclude()
    customerUsername: string | undefined;

    @Exclude()
    isComplete: boolean | undefined;

    @OneToMany(() => Item, (item) => item.ShoppingCart, {
        cascade: [`insert`, `remove`],
    })
    cartItems: Item[] | undefined;

    @CreateDateColumn({
        type: dbType === `sqlite` ? `datetime` : `timestamp`,
    })
    created: Date | undefined;

    @UpdateDateColumn({
        type: dbType === `sqlite` ? `datetime` : `timestamp`,
    })
    updated: Date | undefined;
}
