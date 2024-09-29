import {
    IsArray,
    IsUUID,
    ValidateNested,
    ArrayMinSize, IsInt, Min
} from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';
import { Type } from 'class-transformer';
import { ItemDTO } from './itemDTO.entity';
import { OrderStatus } from '../entity/order.entity';


export class OrderDTO {
    @IsUUID()
    @ApiProperty({ example: '00000000-0000-0000-0000-000000000000' })
    readonly customerId!: string;

    @IsArray()
    @ValidateNested({ each: true })
    @ArrayMinSize(1)
    @Type(() => ItemDTO)
    @ApiProperty({ type: [ItemDTO] })
    readonly items!: ItemDTO[];
}


export class OrderUpdateDTO {
    @IsUUID()
    readonly id!: string;

    @IsInt()
    @Min(0)
    readonly version!: number;

    @IsUUID()
    @ApiProperty({ example: '00000000-0000-0000-0000-000000000000' })
    readonly customerId!: string;

    readonly status!: OrderStatus
}