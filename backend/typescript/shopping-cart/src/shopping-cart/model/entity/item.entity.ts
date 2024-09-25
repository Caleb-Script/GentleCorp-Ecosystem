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
import { ShoppingCart } from './shopping-cart.entity';
import { Exclude } from 'class-transformer';

@Entity()
export class Item {
    @PrimaryGeneratedColumn()
    id: string | undefined;

    @VersionColumn()
    version: number | undefined;

    @Exclude()
    skuCode: string;

    @Column()
    quantity: number;

    @Column()
    inventoryId: string | undefined;

    // @Column('decimal', {
    //     precision: 8,
    //     scale: 2,
    //     transformer: new DecimalTransformer(),
    // })
    @Exclude()
    price: number;

    @Exclude()
    name: string;

    @CreateDateColumn({
        type: 'timestamp',
    })
    readonly created: Date | undefined;

    @UpdateDateColumn({
        type: `timestamp`,
    })
    readonly updated: Date | undefined;

    @ManyToOne(() => ShoppingCart, (shoppingCart) => shoppingCart.cartItems)
    @JoinColumn({ name: 'shopping_cart_id' })
    shoppingCart: ShoppingCart | undefined;

    // toString Methode anpassen
    public toString = (): string => {
        return JSON.stringify({
            id: this.id,
            version: this.version,
            skuCode: this.skuCode,
            quantity: this.quantity,
            inventoryId: this.inventoryId,
            price: this.price,
            name: this.name,
            created: this.created,
            updated: this.updated,
            shoppingCartId: this.shoppingCart?.id,
        });
    }
}