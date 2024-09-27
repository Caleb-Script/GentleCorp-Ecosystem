import { ForbiddenException, Injectable, NotFoundException, UnauthorizedException, UnprocessableEntityException } from '@nestjs/common';
import { getLogger } from '../../logger/logger';
import { ShoppingCart } from '../model/entity/shopping-cart.entity';
import { ShoppingCartQueryBuilder } from './query-builder';
import { SearchCriteria } from '../model/searchCriteria';
import { CustomerRepository } from '../clients/customer/customer.repository';
import { Item } from '../model/entity/item.entity';
import { Inventory } from '../clients/inventory/inventory.interface';
import { InventoryRepository } from '../clients/inventory/inventory.repository';
import { KeycloakService } from '../../keycloak/keycloak.service';

export interface FindByIdParams {
    readonly id: string;
    readonly withItems: boolean | undefined;
}

export interface FindParams {
    readonly searchCriteria?: SearchCriteria;
}

@Injectable()
export class ShoppingCartReadService {
    static readonly ID_PATTERN =
        /^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$/u;

    readonly #shoppingCartProps: string[];
    readonly #queryBuilder: ShoppingCartQueryBuilder;
    readonly #logger = getLogger(ShoppingCartReadService.name);
    readonly #customerRepository: CustomerRepository;
    readonly #inventoryRepository: InventoryRepository;
    readonly #keycloakService: KeycloakService;

    constructor(queryBuilder: ShoppingCartQueryBuilder, customerRepository: CustomerRepository, inventoryRepository: InventoryRepository, keycloakService: KeycloakService) {
        const shoppingCartDummy = new ShoppingCart();
        this.#shoppingCartProps = Object.getOwnPropertyNames(shoppingCartDummy);
        this.#queryBuilder = queryBuilder;
        this.#inventoryRepository = inventoryRepository;
        this.#customerRepository = customerRepository;
        this.#keycloakService = keycloakService;
    }

    async findById({ id, withItems, authorization }: { id: string, withItems: boolean, authorization: string | undefined }) {
        this.#logger.debug('findById: id=%s, withItems=%s', id, withItems);

        if (id === undefined || id === null) {
            throw new NotFoundException('Shopping ShoppingCart with ID %s not found', id);
        }

        const cart: ShoppingCart = await this.#queryBuilder
            .buildId({ id, withItems })
            .getOne();

        if (cart === null) {
            throw new NotFoundException(`No cart found with ID ${id}.`);
        }

        let token = authorization;
        const { roles, username } = this.#keycloakService.extractRolesAndUsernameFromToken(token);
        const shoppingCartUsername = await this.getCustomer({ customerId: cart.customerId, token })
        const isAdmin = roles.includes('gentlecorp-admin');

        this.#logger.debug('findById: isAdmin=%s', isAdmin);

        if (shoppingCartUsername != username && !isAdmin) {
            throw UnauthorizedException
        }
       

        token = isAdmin ? token : 'Bearer ' + await this.getAdminToken();
        this.#logger.debug('findById: token=%s', token);

        cart.customerUsername = isAdmin ? await this.getCustomer({ customerId: cart.customerId, token }) : username;



        this.#logger.debug('findById: cart=%s', cart);
        const items: Item[] = cart.cartItems;
        const cartItems: Item[] = await this.getInventoryDetails({ items, token });
        cart.cartItems = cartItems;
        const totalPrice = await this.getTotal(cartItems);
        cart.totalAmount = totalPrice;
        cart.isComplete = items.length > 0 ? false : true;
        
        return cart;
    }

    async find( searchCriteria?: SearchCriteria) {
        const withItems = false;
        this.#logger.debug('find: searchCriteria=%s, withItems=%s', searchCriteria, withItems);

        if (searchCriteria === undefined) {
            const carts: ShoppingCart[] = await this.#queryBuilder
                .build(withItems, {})
                .getMany();

            this.#logger.debug('find: carts=%o', carts);
            return carts;
        }

        const keys: string[] = Object.keys(searchCriteria);
        if (keys.length === 0) {
            return this.#queryBuilder.build(withItems, searchCriteria).getMany();
        }

        const carts: ShoppingCart[] = await this.#queryBuilder
            .build(withItems, searchCriteria)
            .getMany();

        // if (searchCriteria.isComplete !== undefined || searchCriteria.totalAmount !== undefined) {
        //     this.#logger.debug('find: searchCriteria isComplete=%s, totalAmount=%s', searchCriteria.isComplete, searchCriteria.totalAmount);
        //     return carts.filter(cart => {
        //         cart.isComplete = cart.cartItems.length > 0 ? false : true;
        //         const isCompleteMatch = searchCriteria.isComplete === undefined || cart.isComplete === searchCriteria.isComplete;
        //         const totalAmountMatch = searchCriteria.totalAmount === undefined ||
        //             (cart.totalAmount >= searchCriteria.totalAmount);

        //         return isCompleteMatch && totalAmountMatch;
        //     });
        // }

        this.#logger.debug('find: carts=%o', carts);
        return carts;
    }

    async getTotal(items: Item[]) {
        this.#logger.debug('getTotal: items=%s', items);

        const totalPrice = items.reduce((total, item) => {
            const itemPrice = item.price || 0;
            return total + (itemPrice * item.quantity);
        }, 0);

        return parseFloat(totalPrice.toFixed(2));
    }

    async getCustomer({ customerId, token }: { customerId: string, token?: string }) {
        this.#logger.debug('getCustomer: customerId=%s', customerId);

        try {
            const customer = await this.#customerRepository.getById(customerId, "-1", token);
            this.#logger.debug('getCustomer: customer=%s', customer);
            return customer.username;
        } catch (error) {
            if (error.response && error.response.status === 403) {
                throw new ForbiddenException('Access denied to customer data.');
            }
            if (error.response && error.response.status === 404) {
                throw new NotFoundException(`Customer with ID ${customerId} not found.`);
            }
            if (error.response && error.response.status === 401) {
                throw new UnauthorizedException('Access denied to customer data.');
            }
            this.#logger.error('getCustomer: error=%s', error.message);
            throw new NotFoundException(`Customer with ID ${customerId} not found.`);
        }
    }

    async getInventoryDetails({ items, token }: { items: Item[], token?: string }) {
        this.#logger.debug('getInventory: items=%s', items);

        if (!items || items.length === 0) {
            this.#logger.warn('getInventory: No items provided');
            return [];
        }

        const inventoryDetails = await Promise.all(items.map(async (item) => {
            const inventoryData: Inventory = await this.#inventoryRepository.getById(item.inventoryId, "-1", token);

            return {
                ...item,
                price: inventoryData.unit_price,
                skuCode: inventoryData.sku_code,
                name: inventoryData.name,
            };
        }));

        this.#logger.debug('getInventory: inventoryDetails=%o', inventoryDetails);
        return inventoryDetails;
    }

    async existInventory(item: string, token: string ) {
        this.#logger.debug('getInventory: item=%s, token=%s', item, token);

        try {
            await this.#inventoryRepository.getById(item, "-1", token);
        } catch (error) {
            if (error.response && error.response.status === 403) {
                throw new ForbiddenException('Access denied to customer data.');
            }
            if (error.response && error.response.status === 404) {
                throw new NotFoundException(`Inventory with ID ${item} not found.`);
            }
            if (error.response && error.response.status === 401) {
                throw new UnauthorizedException('Access denied to customer data.');
            }
            if (error.response && error.response.status === 422) {
                throw new UnprocessableEntityException(`${item} is not a valid ID.`);
            }
            this.#logger.error('getCustomer: error=%s', error.message);
            throw new NotFoundException(`Inventory with ID ${item} not found.`);
        }
    }

    async getAdminToken() {
        const token = await this.#keycloakService.login({ username: 'admin', password: 'p' });
        this.#logger.debug('getAdminToken: token=%o', token);
        return token.access_token as string;
    }
}