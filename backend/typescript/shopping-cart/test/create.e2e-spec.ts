import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import request from 'supertest';
import { HttpStatus } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { getAuthorizationTokens } from './clients';

describe('ShoppingCartWriteController (e2e)', () => {
    let app: INestApplication;
    let tokens: any;

    beforeAll(async () => {
        const moduleFixture: TestingModule = await Test.createTestingModule({
            imports: [AppModule],
        }).compile();

        app = moduleFixture.createNestApplication();
        await app.init();

        tokens = await getAuthorizationTokens(app);
    });

    afterAll(async () => {
        await app.close();
    });

    const adminCart = '01000000-0000-0000-0000-000000000000';
    const erikCart = '01000000-0000-0000-0000-000000000005';
    const notFoundCart = '11000000-0000-0000-0000-000000000005';

    const shoppingCartDTO = {
        customerId: '00000000-0000-0000-0000-000000000021'
    };

    const invalidShoppingCartDTO = {
        customerId: '21'
    };

    const existingItem = {
        quantity: 10,
        inventoryId: '80000000-0000-0000-0000-000000000000'
    }

    const newItem = {
        quantity: 5,
        inventoryId: '80000000-0000-0000-0000-000000000004'
    }

    const notFoundItem = {
        quantity: 5,
        inventoryId: '80000000-0000-0000-0000-100000000004'
    }

    const invalidItem = {
        quantity: 5,
        inventoryId: '80000000-0000-0000-0000-00000000004'
    }


    it('should create a shopping cart as admin', async () => {
        const response = await request(app.getHttpServer())
            .post('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .send(shoppingCartDTO)
            .expect(HttpStatus.CREATED);

        expect(response.headers.location).toBeDefined();
        expect(response.headers.location).toMatch(/\/shoppingCart\/[0-9a-fA-F-]+/);
    });

    it('should return FORBIDDEN when creating a shopping cart as a non-admin', async () => {
        const response = await request(app.getHttpServer())
            .post('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken) // Verwenden Sie einen Token für einen nicht-admin Benutzer
            .send(shoppingCartDTO)
            .expect(HttpStatus.FORBIDDEN);

        expect(response.body).toHaveProperty('message', 'Forbidden resource');
    });

    it('should return BAD_REQUEST when shoppingCartDTO is invalid', async () => {
        const response = await request(app.getHttpServer())
            .post('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .send(invalidShoppingCartDTO)
            .expect(HttpStatus.NOT_FOUND);

        expect(response.body).toHaveProperty('message', `Customer with ID ${invalidShoppingCartDTO.customerId} not found.`);
    });







    //Add
    it('add new item to Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"0\"')
            .send(newItem)
            .expect(HttpStatus.CREATED);

        expect(response.headers.location).toBeDefined();
    });

    it('add quantity to existing item to Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"1\"')
            .send(existingItem)
            .expect(HttpStatus.CREATED);

        expect(response.headers.location).toBeDefined();
    });

    it('add quantity to existing item to erik Shopping Cart as Erik', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${erikCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"0\"')
            .send(existingItem)
            .expect(HttpStatus.CREATED);

        expect(response.headers.location).toBeDefined();
    });

    it('add new item to erik Shopping Cart as Erik', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${erikCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"1\"')
            .send(newItem)
            .expect(HttpStatus.CREATED);

        expect(response.headers.location).toBeDefined();
    });

    it('add quantity to existing item to Shopping Cart as basic', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"1\"')
            .send(existingItem)
            .expect(HttpStatus.FORBIDDEN);

        expect(response.body).toHaveProperty('message', 'Access denied to customer data.');
    });

    it('add  item to Shopping Cart NOT FOUND', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"2\"')
            .send(notFoundItem)
            .expect(HttpStatus.NOT_FOUND);

        expect(response.body).toHaveProperty('message', `Inventory with ID ${notFoundItem.inventoryId} not found.`);
    });

    it('add  item to Shopping Cart INVALID ID', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"2\"')
            .send(invalidItem)
            .expect(HttpStatus.UNPROCESSABLE_ENTITY);
        
        expect(response.body).toHaveProperty('message', `${invalidItem.inventoryId} is not a valid ID.`);
    });

    it('add new item to Shopping Cart NO Version', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .send(newItem)
            .expect(HttpStatus.PRECONDITION_REQUIRED);
    });

    it('add new item to Shopping Cart WRONG Version', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/add`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"-1\"')
            .send(newItem)
            .expect(HttpStatus.CONFLICT);
        
        expect(response.body).toHaveProperty('message', 'Shopping cart version mismatch.');
    });



    //Remove
    it('remove the new item from the Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"2\"')
            .send(newItem)
            .expect(HttpStatus.NO_CONTENT);
    });

    it('remove quantity from the Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"3\"')
            .send(existingItem)
            .expect(HttpStatus.NO_CONTENT);
    });

    it('remove quantity from the Shopping Cart as Erik', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"0\"')
            .send(existingItem)
            .expect(HttpStatus.FORBIDDEN);
    });

    it('remove quantity from Erik\'s Shopping Cart as Erik', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${erikCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"2\"')
            .send(newItem)
            .expect(HttpStatus.NO_CONTENT);
    });

    it('remove not existing from the Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"4\"')
            .send(newItem)
            .expect(HttpStatus.NOT_FOUND);

        expect(response.body).toHaveProperty('message', `Item with ID ${newItem.inventoryId} not found in cart.`);
    });

    it('remove Item from the Shopping Cart NO Version', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .send(newItem)
            .expect(HttpStatus.PRECONDITION_REQUIRED);
    });

    it('remove Item from the Shopping Cart wrong version Version', async () => {
        const response = await request(app.getHttpServer())
            .put(`/shoppingCart/${adminCart}/remove`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"-1\"')
            .send(newItem)
            .expect(HttpStatus.CONFLICT);
    });
    





    //Delete
    it('delete admin Shopping Cart NO VERSION', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${adminCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .expect(HttpStatus.PRECONDITION_REQUIRED);
    });

    it('delete admin Shopping Cart WRONG VERSION', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${adminCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"-1\"')
            .expect(HttpStatus.CONFLICT);

        expect(response.body).toHaveProperty('message', 'Shopping cart version mismatch.');
    });


    it('delete admin Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${adminCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"4\"')
            .expect(HttpStatus.NO_CONTENT);
    });

    it('delete admin Shopping Cart as erik', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${adminCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"2\"')
            .expect(HttpStatus.FORBIDDEN);

        expect(response.body).toHaveProperty('message', 'Forbidden resource');
    });

    it('delete admin Shopping Cart as erik', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${erikCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-Match', '\"2\"')
            .expect(HttpStatus.FORBIDDEN);

        expect(response.body).toHaveProperty('message', 'Forbidden resource');
    });

    it('delete NOT FOUND Shopping Cart', async () => {
        const response = await request(app.getHttpServer())
            .delete(`/shoppingCart/${notFoundCart}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-Match', '\"2\"')
            .expect(HttpStatus.NOT_FOUND);

        expect(response.body).toHaveProperty('message', `No cart found with ID ${notFoundCart}.`);
    });

});