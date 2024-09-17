from fastapi import HTTPException, Depends
from fastapi.security import OAuth2AuthorizationCodeBearer
from keycloak import KeycloakOpenID

from ..core import settings

# Keycloak configuration
keycloak_openid = KeycloakOpenID(
    server_url=settings.KEYCLOAK_SERVER_URL,
    client_id=settings.KEYCLOAK_CLIENT_ID,
    realm_name=settings.KEYCLOAK_REALM,
    client_secret_key=settings.KEYCLOAK_CLIENT_SECRET,
)

oauth2_scheme = OAuth2AuthorizationCodeBearer(
    authorizationUrl=settings.KEYCLOAK_AUTH_URL,
    tokenUrl=settings.KEYCLOAK_TOKEN_URL,
)
