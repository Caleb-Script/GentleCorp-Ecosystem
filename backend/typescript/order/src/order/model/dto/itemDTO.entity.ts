import {
    IsString,
    IsNumber,
    IsPositive,
    IsInt,
    Min,
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ItemDTO {
    @IsString()
    @ApiProperty({ example: 'SKU-12345' })
    readonly skuCode!: string;

    @IsNumber()
    @IsPositive()
    @ApiProperty({ example: 19.99 })
    readonly price!: number;

    @IsInt()
    @Min(1)
    @ApiProperty({ example: 2 })
    readonly quantity!: number;
}