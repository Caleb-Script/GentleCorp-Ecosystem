from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from app.models.user import LoginDTO
from app.security.security import keycloak_openid


router = APIRouter()


@router.post("/login")
def login(login_dto: LoginDTO):
    try:
        # Anmeldeanfrage an Keycloak senden
        token_response = keycloak_openid.token(
            grant_type="password",
            username=login_dto.username,
            password=login_dto.password,
            client_id="gentlecorp-client",
            client_secret_key="FiAT2ma40CGVmctDNOOG9h1XtlAqA7Vb",
        )
        return token_response
    except Exception as e:
        raise HTTPException(status_code=401, detail="Invalid credentials")
