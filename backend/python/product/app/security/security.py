from fastapi import Request, HTTPException, Depends
from fastapi.security import OAuth2PasswordBearer
from fastapi.security import OAuth2AuthorizationCodeBearer
from fastapi.security import OAuth2PasswordRequestForm
from keycloak import KeycloakOpenID
from pydantic import BaseModel
from typing import List

from app.models.user import User

# Keycloak configuration
keycloak_openid = KeycloakOpenID(
    server_url="http://localhost:8880/",
    client_id="gentlecorp-client",
    realm_name="GentleCorp-Ecosystem",
    client_secret_key="FiAT2ma40CGVmctDNOOG9h1XtlAqA7Vb"
)

oauth2_scheme = OAuth2AuthorizationCodeBearer(
    authorizationUrl="http://localhost:8080/auth/realms/GentleCorp-Ecosystem/protocol/openid-connect/auth",
    tokenUrl="http://localhost:8080/auth/realms/GentleCorp-Ecosystem/protocol/openid-connect/token"
)
ADMIN = "ADMIN"
USER = "USER"
SUPREME = "SUPREME"
ELITE = "ELITE"
BASIC = "BASIC"


# 2. Get User Information from JWT
def get_current_user(token: str = Depends(oauth2_scheme)) -> User:
    try:
        user_info = keycloak_openid.userinfo(token)
        token_info = keycloak_openid.decode_token(token)
        return User(
            sub=user_info.get("sub"),
            username=user_info.get("preferred_username"),
            roles=token_info.get("realm_access", {}).get("roles", []),
        )
    except Exception as e:
        raise HTTPException(status_code=401, detail="Invalid token")


# 3. Get Role based on Keycloak Realm Access
def get_role(user: User) -> str:
    roles = user.roles
    if ADMIN in roles:
        return "ADMIN"
    if USER in roles:
        return "USER"
    if SUPREME in roles:
        return "SUPREME"
    if ELITE in roles:
        return "ELITE"
    return "BASIC"


# 4. Role-Based Access Control
def requires_role(required_role: str):
    def role_checker(current_user: User = Depends(get_current_user)):
        if required_role not in current_user.roles:
            raise HTTPException(status_code=403, detail="Not enough permissions")
        return current_user

    return role_checker
