import { env } from './env';
import { httpsOptions } from './https';
import {
  type KeycloakConnectConfig,
  PolicyEnforcementMode,
  TokenValidation,
} from 'nest-keycloak-connect';
import { Agent } from 'node:https';
import { config } from './order';

const { keycloak } = config;
const authServerUrl =
  (keycloak?.authServerUrl as string | undefined) ?? 'http://localhost:8080';
// Keycloak ist in Sicherheits-Bereich (= realms) unterteilt
const realm = (keycloak?.realm as string | undefined) ?? 'GentleCorp-Ecosystem';
const clientId = keycloak?.clientId as string | undefined;
const tokenValidation =
  (keycloak?.tokenValidation as TokenValidation | undefined) ??
  (TokenValidation.ONLINE as TokenValidation);

const { CLIENT_SECRET, NODE_ENV } = env;

// https://github.com/ferrerojosh/nest-keycloak-connect/blob/master/README.md#nest-keycloak-options
export const keycloakConnectOptions: KeycloakConnectConfig = {
  authServerUrl,
  realm,
  clientId,
  secret:
    CLIENT_SECRET ?? 'ERROR: Umgebungsvariable CLIENT_SECRET nicht gesetzt!',
  policyEnforcement: PolicyEnforcementMode.PERMISSIVE,
  tokenValidation,
};
if (NODE_ENV === 'development') {
  console.debug('keycloakConnectOptions = %o', keycloakConnectOptions);
}
// else {
//   const { secret, ...keycloakConnectOptionsLog } = keycloakConnectOptions;
//   console.debug('keycloakConnectOptions = %o', keycloakConnectOptionsLog);
// }

/** Pfade für den REST-Client zu Keycloak */
export const paths = {
  accessToken: `realms/${realm}/protocol/openid-connect/token`,
  userInfo: `realms/${realm}/protocol/openid-connect/userinfo`,
  introspect: `realms/${realm}/protocol/openid-connect/token/introspect`,
};

/** Agent für Axios für Requests bei selbstsigniertem Zertifikat */
export const httpsAgent = new Agent({
  requestCert: true,
  rejectUnauthorized: false,
  ca: httpsOptions.cert as Buffer,
});
