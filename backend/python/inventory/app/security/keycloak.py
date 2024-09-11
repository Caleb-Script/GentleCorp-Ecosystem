from fastapi import Depends
from fastapi_keycloak import FastAPIKeycloak
from app.core.config import settings

keycloak_openid = FastAPIKeycloak(
    server_url=settings.KEYCLOAK_SERVER_URL,
    client_id=settings.KEYCLOAK_CLIENT_ID,
    client_secret=settings.KEYCLOAK_CLIENT_SECRET,
    realm_name="GentleCorp-Ecosystem",
    callback_uri="http://localhost:8000/callback",
)


def get_current_user(token: str = Depends(keycloak_openid.get_current_user)):
    return token
