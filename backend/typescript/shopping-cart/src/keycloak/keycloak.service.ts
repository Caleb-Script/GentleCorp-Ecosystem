
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
    // https://www.keycloak.org/docs-api/23.0.4/rest-api/index.html
    const authorization = Buffer.from(`${clientId}:${secret}`, 'utf8').toString(
      'base64',
    );
    this.#loginHeaders = {
      Authorization: `Basic ${authorization}`,
      'Content-Type': 'application/x-www-form-urlencoded',
    };

    this.#keycloakClient = axios.create({
      baseURL: authServerUrl,
      // ggf. httpsAgent fuer HTTPS bei selbst-signiertem Zertifikat
    });
    this.#logger.debug('keycloakClient=%o', this.#keycloakClient.defaults);
  }

  createKeycloakConnectOptions(): KeycloakConnectOptions {
    return keycloakConnectOptions;
  }

  async login({ username, password }: Login) {
    this.#logger.debug('login: username=%s', username);
    if (username === undefined || password === undefined) {
      return;
    }

    const loginBody = `grant_type=password&username=${username}&password=${password}`;
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
    this.#logger.debug('login: response.data=%o', response.data);
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

  // Extraktion der Rollen: wird auf Client-Seite benoetigt
  // { ..., "azp": "buch-client", "exp": ..., "resource_access": { "buch-client": { "roles": ["admin"] } ...}
  // azp = authorized party
  #logPayload(response: AxiosResponse<Record<string, string | number>>) {
    // https://www.keycloak.org/docs-api/23.0.6/rest-api/index.html#ClientInitialAccessCreatePresentation
    const { access_token } = response.data;
    // Payload ist der mittlere Teil zwischen 2 Punkten und mit Base64 codiert
    const [, payloadStr] = (access_token as string).split('.');

    // Base64 decodieren
    if (payloadStr === undefined) {
      return;
    }
    const payloadDecoded = atob(payloadStr);

    // JSON-Objekt fuer Payload aus dem decodierten String herstellen

    /* eslint-disable @typescript-eslint/no-unsafe-assignment */
    const payload = JSON.parse(payloadDecoded);
    const { azp, exp, resource_access } = payload;
    this.#logger.debug('#logPayload: exp=%s', exp);
    const { roles } = resource_access[azp];
    /* eslint-enable @typescript-eslint/no-unsafe-assignment */

    this.#logger.debug('#logPayload: roles=%o', roles);
  }
}
