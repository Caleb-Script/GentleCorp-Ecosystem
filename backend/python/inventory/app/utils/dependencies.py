from fastapi import Depends
from app.security.keycloak import get_current_user


def get_current_user_from_token(token: str = Depends(get_current_user)):
    return token
