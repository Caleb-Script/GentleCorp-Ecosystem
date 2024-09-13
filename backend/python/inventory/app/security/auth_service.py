from fastapi import Depends, Header, HTTPException, status
from typing import Optional

from ..core import custom_logger
from .keycloak import oauth2_scheme, keycloak_openid
from .user import Role, User

logger = custom_logger(__name__)


class AuthService:
    @staticmethod
    async def get_bearer_token(authorization: Optional[str] = Header(None)) -> str:
        logger.debug("get_bearer_token: {}", authorization)
        if authorization is None:
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Authorization header missing",
            )

        token_prefix = "Bearer "
        if not authorization.startswith(token_prefix):
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Invalid Authorization header format",
            )

        return authorization[len(token_prefix) :]

    # 2. Get User Information from JWT
    @staticmethod
    def get_current_user(token: str = Depends(oauth2_scheme)) -> User:
        try:
            user_info = keycloak_openid.userinfo(token)
            token_info = keycloak_openid.decode_token(token)

            # Filter only valid roles
            valid_roles = [
                role
                for role in token_info.get("realm_access", {}).get("roles", [])
                if Role.has_value(role)
            ]

            return User(
                sub=user_info.get("sub"),
                username=user_info.get("preferred_username"),
                roles=valid_roles,
            )
        except Exception as e:
            logger.error(f"Error getting user info: {e}")
            raise HTTPException(status_code=401, detail="Invalid token")

    # 3. Get Role based on Keycloak Realm Access
    @staticmethod
    def get_role(user: User) -> Role:
        roles = user.roles
        if Role.ADMIN in roles:
            return Role.ADMIN
        if Role.USER in roles:
            return Role.USER
        if Role.SUPREME in roles:
            return Role.SUPREME
        if Role.ELITE in roles:
            return Role.ELITE
        return Role.BASIC

    # 4. Role-Based Access Control
    @staticmethod
    def requires_role(required_role: Role):
        def role_checker(current_user: User = Depends(AuthService.get_current_user)):
            if required_role not in current_user.roles:
                raise HTTPException(status_code=403, detail="Not enough permissions")
            return current_user

        return role_checker
