from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from ..core import Logger, settings
from ..security import LoginDTO, keycloak_openid

router = APIRouter()
logger = Logger(__name__, color="yellow")


@router.post("/login")
def login(login_dto: LoginDTO):
    logger.debug("Login: {}", login_dto)
    try:
        # Anmeldeanfrage an Keycloak senden
        token_response = keycloak_openid.token(
            grant_type="password",
            username=login_dto.username,
            password=login_dto.password,
            client_id=settings.KEYCLOAK_CLIENT_ID,
            client_secret_key=settings.KEYCLOAK_CLIENT_SECRET,
        )
        logger.success("Successfully authenticated")
        return token_response
    except Exception as e:
        logger.error("Login failed: {}", str(e))
        raise HTTPException(status_code=401, detail="Invalid credentials")
