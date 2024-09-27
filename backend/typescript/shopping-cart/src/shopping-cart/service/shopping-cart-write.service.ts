import { type DeleteResult, Repository } from 'typeorm';
import { ConflictException, Injectable, NotFoundException, UnauthorizedException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { getLogger } from '../../logger/logger';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { ShoppingCartReadService } from './shopping-cart-read.service';
import { Item } from '../model/entity/item.entity';
import {
    VersionInvalidException,
    VersionOutdatedException,
} from './exceptions';
import { ConsumerService } from '../../kafka/Consumer.service';
import { ItemDTO } from '../model/dto/item.dto';
import { KeycloakService } from '../../keycloak/keycloak.service';
import { ShoppingCartDTO } from '../model/dto/shopping-cart.dto';

export interface UpdateParams {
    readonly id: string | undefined;
    readonly shoppingCart: ShoppingCart;
    readonly version: string;
}

@Injectable()
export class ShoppingCartWriteService {
    private static readonly VERSION_PATTERN = /^"\d{1,3}"/u;
    readonly #shoppingCartRepository: Repository<ShoppingCart>;
    readonly #itemRepository: Repository<Item>;
    readonly #readService: ShoppingCartReadService;
    readonly #logger = getLogger(ShoppingCartWriteService.name);
    readonly #keycloakService: KeycloakService;

    constructor(
        @InjectRepository(ShoppingCart)
        shoppingCartRepository: Repository<ShoppingCart>,
        @InjectRepository(Item)
        itemRepository: Repository<Item>,
        private readonly consumerService: ConsumerService,
        readService: ShoppingCartReadService,
        keycloakService: KeycloakService,
    ) {
        this.#shoppingCartRepository = shoppingCartRepository;
        this.#itemRepository = itemRepository;
        this.#readService = readService;
        this.#keycloakService = keycloakService;
    }

    async onModuleInit() {
        await this.consumerService.consume(
            {
                topics: ['create-shopping-cart', 'delete-shopping-cart'],
            },
            {
                eachMessage: async ({ topic, partition, message }) => {
                    try {
                        const messageValue = message.value.toString();
                        this.#logger.debug('Kafka message received', { topic, partition, value: messageValue });
                        const parsedMessage = JSON.parse(messageValue);

                        switch (topic) {
                            case 'create-shopping-cart':
                                const shoppingCartDTO: ShoppingCartDTO = parsedMessage;
                                await this.create(shoppingCartDTO);
                                break;
                            case 'delete-shopping-cart':
                                const { id, token } = parsedMessage;
                                const shoppingCartList = await this.#readService.find({ customerId: id });
                                const shoppingCartId = shoppingCartList[0]?.id;
                                if (shoppingCartId) {
                                    await this.delete(shoppingCartId, token);
                                } else {
                                    this.#logger.warn('No shopping cart found for deletion', { customerId: id });
                                }
                                break;
                            default:
                                this.#logger.error('Unknown topic:', topic);
                        }
                    } catch (err) {
                        this.#logger.error('Error processing Kafka message', err);
                    }
                },
            }
        );
    }

    async create(shoppingCartDTO: ShoppingCartDTO): Promise<string> {
        const shoppingCart = this.#shoppingCartDTOToShoppingCart(shoppingCartDTO);
        this.#logger.debug('create: shoppingCart=%o', shoppingCart);
        const shoppingCartDb = await this.#shoppingCartRepository.save(shoppingCart);
        this.#logger.debug('create: shoppingCartDb=%o', shoppingCartDb);
        return shoppingCartDb.id!;
    }

    #toItem({itemDTO, shoppingCart}: {itemDTO: ItemDTO, shoppingCart: ShoppingCart}): Item {
        return {
            id: undefined,
            version: 0,
            quantity: itemDTO.quantity,
            inventoryId: itemDTO.inventoryId,
            shoppingCart: shoppingCart,
            skuCode: undefined,
            price: undefined,
            name: undefined,
            created: new Date(),
            updated: new Date()
        };
    }

    async addItemToCart({ cartId, itemDTO, token, cartVersion}: { cartId: string, itemDTO: ItemDTO, token: string, cartVersion: number }) {
        let id: string;
        let version: number;

        const { inventoryId, quantity } = itemDTO;
        this.#logger.debug('addItemToCart: cartId=%s, inventoryId=%s, quantity=%d', cartId, inventoryId, quantity);
        const shoppingCart = await this.#readService.findById({ id: cartId, withItems: true, authorization: token });
        const { roles, username } = this.#keycloakService.extractRolesAndUsernameFromToken(token);
        const isAdmin = roles.includes('gentlecorp-admin');

        this.#logger.debug('findById: isAdmin=%s', isAdmin);

        if (shoppingCart.customerUsername != username && !isAdmin) {
            throw UnauthorizedException
        }
        
        this.#logger.debug('addItemToCart: shoppingCart=%s', shoppingCart);

        if (shoppingCart.version !== cartVersion) {
            throw new ConflictException('Shopping cart version mismatch.');
        }

        const itemIndex = shoppingCart.cartItems.findIndex(item => item.inventoryId === inventoryId);
        this.#logger.debug('addItemToCart: itemIndex=%s', itemIndex);
        if (itemIndex > -1) {
            this.#logger.debug('addItemToCart: item=%o', shoppingCart.cartItems[itemIndex])
            shoppingCart.cartItems[itemIndex].quantity += quantity;
            shoppingCart.cartItems[itemIndex].version += 1;
            const updatedItem = await this.#itemRepository.save(shoppingCart.cartItems[itemIndex]);
            id = updatedItem.id;
            version = updatedItem.version;
            this.#logger.debug('addItemToCart: updatedItem=%o', updatedItem);
        } else {
            token = isAdmin ? token : 'Bearer ' + await this.#readService.getAdminToken()
            await this.#readService.existInventory(inventoryId, token)
            const newItem = this.#toItem({itemDTO, shoppingCart})
            const savedItem = await this.#itemRepository.save(newItem);
            shoppingCart.cartItems.push(savedItem);
            id = savedItem.id;
            version = savedItem.version;
            this.#logger.debug('addItemToCart: newItem=%o', savedItem);
        }

        shoppingCart.version++;
        await this.#shoppingCartRepository.save(shoppingCart);
        return { id, version };
    }

    async removeItemFromCart({ cartId, cartVersion, itemDTO, token }: { cartId: string, cartVersion: number, itemDTO:ItemDTO, token: string }): Promise<{ version: number }> {
        let version: number;
        const { inventoryId, quantity } = itemDTO;
        this.#logger.debug('removeItemFromCart: cartId=%s, inventoryId=%s, quantity=%d', cartId, inventoryId, quantity);
        const shoppingCart = await this.#readService.findById({ id: cartId, withItems: true, authorization: token });

        if (shoppingCart.version !== cartVersion) {
            throw new ConflictException('Shopping cart version mismatch.');
        }

        const itemIndex = shoppingCart.cartItems.findIndex(item => item.inventoryId === inventoryId);
        if (itemIndex > -1) {
            // Artikel existiert, Menge reduzieren
            shoppingCart.cartItems[itemIndex].quantity -= quantity;
            if (shoppingCart.cartItems[itemIndex].quantity <= 0) {
                // Artikel entfernen, wenn die Menge 0 oder weniger ist
                shoppingCart.cartItems.splice(itemIndex, 1);
            }
            version = shoppingCart.cartItems[itemIndex]?.version || 0; // Neue Version des Artikels oder 0, wenn entfernt
        } else {
            throw new NotFoundException(`Item with ID ${inventoryId} not found in cart.`);
        }

        shoppingCart.version++;
        await this.#shoppingCartRepository.save(shoppingCart);
        return { version };
    }

    async delete(id: string, authorization: string) {
        this.#logger.debug('delete: id=%s', id);
        const shoppingCart = await this.#readService.findById({ id, withItems: true, authorization });

        
        let deleteResult: DeleteResult | undefined;
        await this.#shoppingCartRepository.manager.transaction(async (transactionalMgr) => {
            const items = shoppingCart.cartItems ?? [];
            for (const item of items) {
                await transactionalMgr.delete(Item, item.id);
            }

            deleteResult = await transactionalMgr.delete(ShoppingCart, id);
            this.#logger.debug('delete: deleteResult=%o', deleteResult);
        });

        return (
            deleteResult?.affected !== undefined &&
            deleteResult.affected > 0
        );
    }

    async #validateUpdate(
        shoppingCart: ShoppingCart,
        id: string,
        versionStr: string,
    ): Promise<ShoppingCart> {
        this.#logger.debug(
            '#validateUpdate: shoppingCart=%o, id=%s, versionStr=%s',
            shoppingCart,
            id,
            versionStr,
        );
        if (!ShoppingCartWriteService.VERSION_PATTERN.test(versionStr)) {
            throw new VersionInvalidException(versionStr);
        }

        const version = Number.parseInt(versionStr.slice(1, -1), 10);
        this.#logger.debug(
            '#validateUpdate: shoppingCart=%o, version=%d',
            shoppingCart,
            version,
        );

        const shoppingCartDb = await this.#readService.findById({ id, withItems: false, authorization: "" });

        const versionDb = shoppingCartDb.version!;
        if (version < versionDb) {
            this.#logger.debug('#validateUpdate: versionDb=%d', version);
            throw new VersionOutdatedException(version);
        }
        this.#logger.debug('#validateUpdate: shoppingCartDb=%o', shoppingCartDb);
        return shoppingCartDb;
    }

    #shoppingCartDTOToShoppingCart(shoppingCartDTO: ShoppingCartDTO): ShoppingCart {
        const shoppingCart = {
            id: undefined,
            version: undefined,
            totalAmount: undefined,
            isComplete: undefined,
            customerUsername: undefined,
            customerId: shoppingCartDTO.customerId,
            cartItems: undefined,
            created: new Date(),
            updated: new Date(),
        };

        return shoppingCart;
    }

    
}
