import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import request from 'supertest';
import { HttpStatus } from '@nestjs/common';
import { AppModule } from '../src/app.module';
import { getAuthorizationTokens } from './clients';

describe('ShoppingCartGetController (e2e)', () => {
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

    it('should get all shopping carts as admin', async () => {
        const response = await request(app.getHttpServer())
            .get('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .expect(HttpStatus.OK);

        expect(response.body).toHaveProperty('_embedded');
        expect(response.body._embedded).toHaveProperty('shoppingCarts');
        expect(Array.isArray(response.body._embedded.shoppingCarts)).toBe(true);
    });

    it('should get all shopping carts as admin', async () => {
        const response = await request(app.getHttpServer())
            .get('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .expect(HttpStatus.OK);

        expect(response.body).toHaveProperty('_embedded');
        expect(response.body._embedded).toHaveProperty('shoppingCarts');
        expect(Array.isArray(response.body._embedded.shoppingCarts)).toBe(true);
    });

    it('should get all shopping carts as customer', async () => {
        const response = await request(app.getHttpServer())
            .get('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .expect(HttpStatus.FORBIDDEN);

        expect(response.status).toBe(HttpStatus.FORBIDDEN);
    });

    it('should get all shopping carts as visitor', async () => {
        const response = await request(app.getHttpServer())
            .get('/shoppingCart')
            .set('Accept', 'application/hal+json')
            .expect(HttpStatus.UNAUTHORIZED);

        expect(response.status).toBe(HttpStatus.UNAUTHORIZED);
    });

    it('should get all completed shopping carts', async () => {
        const response = await request(app.getHttpServer())
            .get('/shoppingCart?isComplete=true')
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .expect(HttpStatus.OK);

        expect(response.body).toHaveProperty('_embedded');
        expect(response.body._embedded).toHaveProperty('shoppingCarts');
        expect(Array.isArray(response.body._embedded.shoppingCarts)).toBe(true);
        //expect(response.body._embedded.shoppingCarts.length).toBe(6);
        expect(response.body._embedded.shoppingCarts[0].isComplete).toBe(true);
    });



    // it('should return 404 for non-existing shopping cart', async () => {
    //     const nonExistingId = 'non-existing-id';
    //     const response = await request(app.getHttpServer())
    //         .get(`/shopping-cart/${nonExistingId}`)
    //         .set('Accept', 'application/hal+json')
    //         .set('Authorization', authorizationToken)
    //         .expect(HttpStatus.NOT_FOUND);

    //     expect(response.body).toHaveProperty('message', 'Shopping cart not found'); // Beispielnachricht, anpassen
    // });




    const shoppingCartId = '01000000-0000-0000-0000-000000000000';
    const shoppingCartNotFoundId = '11000000-0000-0000-0000-000000000001';
    const calebShoppingCartId = '01000000-0000-0000-0000-000000000025';
    const leroyhoppingCartId = '01000000-0000-0000-0000-000000000026';
    const erikShoppingCartId = '01000000-0000-0000-0000-000000000005';

    it('should get shopping cart by id', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.OK);

        expect(response.body).toHaveProperty('_links');
        expect(response.body).toHaveProperty('totalAmount');
        expect(response.body).toHaveProperty('customerId');
        expect(response.body).toHaveProperty('customerUsername');
        expect(response.body).toHaveProperty('isComplete');
        expect(response.body).toHaveProperty('cartItems');
        expect(response.body.totalAmount).toBe(449.98);
        expect(response.body.isComplete).toBe(false);
        expect(response.body.customerUsername).toBe('admin');
    });

    it('should return NOT_MODIFIED if ETag matches', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-None-Match', `"0"`)
            .expect(HttpStatus.NOT_MODIFIED);

        expect(response.status).toBe(HttpStatus.NOT_MODIFIED);
    });

    it('should return PRECONDITION_REQUIRED if ETag matches', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.adminAuthorizationToken)
            .expect(HttpStatus.PRECONDITION_REQUIRED);

        expect(response.status).toBe(HttpStatus.PRECONDITION_REQUIRED);
    });

    it('should return NOT_FOUND for unsupported media type', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartNotFoundId}`)
            .set('Accept', 'application/hal+json') // Nicht unterstützter Typ
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.NOT_FOUND);

        expect(response.status).toBe(HttpStatus.NOT_FOUND);
    });

    it('should return NOT_ACCEPTABLE for unsupported media type', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartId}`)
            .set('Accept', 'application/atom+xml') // Nicht unterstützter Typ
            .set('Authorization', tokens.adminAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.NOT_ACCEPTABLE);

        expect(response.status).toBe(HttpStatus.NOT_ACCEPTABLE);
    });

    it('should return FORBIDDEN', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${shoppingCartId}`)
            .set('Accept', 'application/hal+json') // Nicht unterstützter Typ
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.FORBIDDEN);

        expect(response.status).toBe(HttpStatus.FORBIDDEN);
    });

    it('should return shopping cart for Caleb as Caleb', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${calebShoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.supremeAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.OK);

        expect(response.status).toBe(HttpStatus.OK);
        expect(response.body).toHaveProperty('_links');
        expect(response.body).toHaveProperty('totalAmount');
        expect(response.body).toHaveProperty('customerId');
        expect(response.body).toHaveProperty('customerUsername');
        expect(response.body).toHaveProperty('isComplete');
        expect(response.body).toHaveProperty('cartItems');
        expect(response.body.totalAmount).toBe(2374.86);
        expect(response.body.isComplete).toBe(false);
        expect(response.body.customerUsername).toBe('gentlecg99');
    });

    it('should return shopping cart for leroy as Leroy', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${leroyhoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.eliteAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.OK);

        expect(response.status).toBe(HttpStatus.OK);
        expect(response.body).toHaveProperty('_links');
        expect(response.body).toHaveProperty('totalAmount');
        expect(response.body).toHaveProperty('customerId');
        expect(response.body).toHaveProperty('customerUsername');
        expect(response.body).toHaveProperty('isComplete');
        expect(response.body).toHaveProperty('cartItems');
        expect(response.body.totalAmount).toBe(50.97);
        expect(response.body.isComplete).toBe(false);
        expect(response.body.customerUsername).toBe('leroy135');
    });

    it('should return shopping cart for erik as erik', async () => {
        const response = await request(app.getHttpServer())
            .get(`/shoppingCart/${erikShoppingCartId}`)
            .set('Accept', 'application/hal+json')
            .set('Authorization', tokens.basicAuthorizationToken)
            .set('If-None-Match', `"1"`)
            .expect(HttpStatus.OK);

        expect(response.status).toBe(HttpStatus.OK);
        expect(response.body).toHaveProperty('_links');
        expect(response.body).toHaveProperty('totalAmount');
        expect(response.body).toHaveProperty('customerId');
        expect(response.body).toHaveProperty('customerUsername');
        expect(response.body).toHaveProperty('isComplete');
        expect(response.body).toHaveProperty('cartItems');
        expect(response.body.totalAmount).toBe(431.94);
        expect(response.body.isComplete).toBe(false);
        expect(response.body.customerUsername).toBe('erik');
    });
});