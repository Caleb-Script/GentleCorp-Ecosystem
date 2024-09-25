import { Injectable } from '@nestjs/common';
import axios, {
  type AxiosInstance,
  type AxiosResponse,
  type RawAxiosRequestHeaders,
} from 'axios';
import {
  type KeycloakConnectOptions,
  type KeycloakConnectOptionsFactory,
} from 'nest-keycloak-connect';
import { keycloakConnectOptions, paths } from '../config/keycloak';
import { getLogger } from '../logger/logger';

const { authServerUrl, clientId, secret } = keycloakConnectOptions;

interface Login {
  readonly username: string | undefined;
  readonly password: string | undefined;
}

@Injectable()
export class KeycloakService implements KeycloakConnectOptionsFactory {
  readonly #loginHeaders: RawAxiosRequestHeaders;
  readonly #keycloakClient: AxiosInstance;
  readonly #logger = getLogger(KeycloakService.name);

  constructor() {
    const authorization = Buffer.from(`${clientId}:${secret}`, 'utf8').toString('base64');
    this.#loginHeaders = {
      Authorization: `Basic ${authorization}`,
      'Content-Type': 'application/x-www-form-urlencoded',
    };

    this.#keycloakClient = axios.create({
      baseURL: authServerUrl,
    });
    this.#logger.debug('keycloakClient=%o', this.#keycloakClient.defaults);
  }

  createKeycloakConnectOptions(): KeycloakConnectOptions {
    return keycloakConnectOptions;
  }

  async login({ username, password }: Login) {
    this.#logger.debug('login: username=%s, password=%s', username, password);
    if (username === undefined || password === undefined) {
      return;
    }

    const loginBody = `grant_type=password&username=${username}&password=${password}&scope=openid`;
    let response: AxiosResponse<Record<string, number | string>>;
    try {
      response = await this.#keycloakClient.post(paths.accessToken, loginBody, {
        headers: this.#loginHeaders,
      });
    } catch {
      this.#logger.warn('login: Fehler bei %s', paths.accessToken);
      return;
    }

    this.#logPayload(response);
    return response.data;
  }

  async refresh(refresh_token: string | undefined) {
    this.#logger.debug('refresh: refresh_token=%s', refresh_token);
    if (refresh_token === undefined) {
      return;
    }

    const refreshBody = `grant_type=refresh_token&refresh_token=${refresh_token}`;
    let response: AxiosResponse<Record<string, number | string>>;
    try {
      response = await this.#keycloakClient.post(
        paths.accessToken,
        refreshBody,
        { headers: this.#loginHeaders },
      );
    } catch {
      this.#logger.warn(
        'refresh: Fehler bei POST-Request: path=%s, body=%o',
        paths.accessToken,
        refreshBody,
      );
      return;
    }
    this.#logger.debug('refresh: response.data=%o', response.data);
    return response.data;
  }

  #logPayload(response: AxiosResponse<Record<string, string | number>>) {
    const { access_token } = response.data;
    const [, payloadStr] = (access_token as string).split('.');

    if (payloadStr === undefined) {
      return;
    }
    const payloadDecoded = atob(payloadStr);

    let payload;
    try {
      payload = JSON.parse(payloadDecoded);
    } catch (error) {
      this.#logger.warn('Failed to parse JWT payload: %s', error);
      return;
    }

    const { azp, exp, resource_access } = payload;
    this.#logger.debug('#logPayload: exp=%s', exp);
    const { roles } = resource_access[azp];
    this.#logger.debug('#logPayload: roles=%o', roles);
  }

  extractRolesAndUsernameFromToken(token: string): { roles: string[]; username?: string } {
    const parts = token.split('.');
    if (parts.length !== 3) {
      this.#logger.warn('Invalid JWT token format');
      return { roles: [] }; // Nur Rollen zurückgeben
    }

    const payloadStr = parts[1];
    const payloadDecoded = atob(payloadStr);

    let payload;
    try {
      payload = JSON.parse(payloadDecoded);
    } catch (error) {
      this.#logger.warn('Failed to parse JWT payload: %s', error);
      return { roles: [] }; // Nur Rollen zurückgeben
    }

    const { resource_access, preferred_username } = payload; // preferred_username extrahieren
    const roles: string[] = [];

    if (resource_access) {
      for (const key in resource_access) {
        if (resource_access[key].roles) {
          roles.push(...resource_access[key].roles);
        }
      }
    }

    return { roles, username: preferred_username }; // Rollen und Benutzernamen zurückgeben
  }
}