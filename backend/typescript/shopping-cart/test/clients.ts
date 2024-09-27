import { INestApplication } from '@nestjs/common';
import request from 'supertest';
import { HttpStatus } from '@nestjs/common';
import { paths } from '../src/config/paths';

const USERNAME_ADMIN = 'admin';
const USERNAME_USER = 'user';
const USERNAME_SUPREME = 'gentlecg99';
const USERNAME_ELITE = 'leroy135';
const USERNAME_BASIC = 'erik';

const PASSWORD = 'p';

export const loginPath = `/${paths.auth}/${paths.login}`;

interface LoginResult {
    access_token: string;
}

export const loginRest = async (
    app: INestApplication,
    username: string,
    password: string,
): Promise<string> => {
    const response = await request(app.getHttpServer())
        .post(loginPath)
        .send(`username=${username}&password=${password}`)
        .set('Content-Type', 'application/x-www-form-urlencoded')
        .expect(HttpStatus.OK);

    return response.body.access_token;
};

export const getAuthorizationTokens = async (app: INestApplication) => {
    const adminAuthorizationToken = `Bearer ${await loginRest(app, USERNAME_ADMIN, PASSWORD)}`;
    const userAuthorizationToken = `Bearer ${await loginRest(app, USERNAME_USER, PASSWORD)}`;
    const basicAuthorizationToken = `Bearer ${await loginRest(app, USERNAME_BASIC, PASSWORD)}`;
    const supremeAuthorizationToken = `Bearer ${await loginRest(app, USERNAME_SUPREME, PASSWORD)}`;
    const eliteAuthorizationToken = `Bearer ${await loginRest(app, USERNAME_ELITE, PASSWORD)}`;

    return {
        adminAuthorizationToken,
        userAuthorizationToken,
        basicAuthorizationToken,
        supremeAuthorizationToken,
        eliteAuthorizationToken,
    };
};