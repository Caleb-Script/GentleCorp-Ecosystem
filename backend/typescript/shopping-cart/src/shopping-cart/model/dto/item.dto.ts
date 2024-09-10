import { MaxLength, Min } from 'class-validator';

/**
 * Entity-Klasse für Abbildung ohne TypeORM.
 */
export class ItemDTO {
    @MaxLength(32)
    readonly skuCode!: string;

    @Min(0)
    readonly quantity: number;

    @MaxLength(255)
    readonly productId!: string;
}
