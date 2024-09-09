import {
    CreateDateColumn,
    Entity,
    OneToMany,
    PrimaryGeneratedColumn,
    UpdateDateColumn,
    VersionColumn,
} from 'typeorm';

@Entity()
export class ShoppingCart {
    @PrimaryGeneratedColumn(`uuid`)
    shoppingCart: string | undefined;

    @VersionColumn()
    version: number | undefined;

    totalAmount: number | undefined;

    customerId: string | undefined;

    customerUsername: string | undefined;

    isComplete: boolean | undefined;

    @OneToMany(() => Item, (item) => item.shoppingCart, {
        cascade: [`insert`, `remove`],
    })
    items: Item[] | undefined;

    @CreateDateColumn({
        type: dbType === `sqlite` ? `datetime` : `timestamp`,
    })
    created: Date | undefined;

    @UpdateDateColumn({
        type: dbType === `sqlite` ? `datetime` : `timestamp`,
    })
    updated: Date | undefined;
}
