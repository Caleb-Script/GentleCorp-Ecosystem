from pydantic import ConfigDict
from pydantic_settings import BaseSettings
from dotenv import load_dotenv

load_dotenv()


class Settings(BaseSettings):
    PROJECT_NAME: str = "Product Service"
    DATABASE_URL: str
    DATABASE_NAME: str
    KEYCLOAK_SERVER_URL: str
    KEYCLOAK_REALM: str
    KEYCLOAK_CLIENT_ID: str
    KEYCLOAK_CLIENT_SECRET: str
    KEYCLOAK_AUTH_URL: str
    KEYCLOAK_TOKEN_URL: str
    LOG_LEVEL: str
    APP_ENV: str = "development"
    APP_DEBUG: bool = True

    class Config:
        env_file = "/.env"  # Make sure this points to your .env file if you have one


settings = Settings()
