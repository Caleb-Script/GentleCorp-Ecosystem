from pydantic_settings import BaseSettings
from dotenv import load_dotenv
import os

# Laden Sie die .env-Datei
load_dotenv(dotenv_path=".env")
print("PRODUCT_SERVICE_PORT from .env:", os.getenv("PRODUCT_SERVICE_PORT"))

class Settings(BaseSettings):
    PROJECT_NAME: str = "Inventory Service"
    DATABASE_URL: str
    ADMIN_DATABASE_URL: str
    KEYCLOAK_SERVER_URL: str
    KEYCLOAK_REALM: str
    KEYCLOAK_CLIENT_ID: str
    KEYCLOAK_CLIENT_SECRET: str
    KEYCLOAK_AUTH_URL: str
    KEYCLOAK_TOKEN_URL: str
    LOG_LEVEL: str
    APP_ENV: str = "development"
    APP_DEBUG: bool = True
    PRODUCT_SERVICE_SCHEMA: str
    PRODUCT_SERVICE_HOST: str
    PRODUCT_SERVICE_PORT: str 

    class Config:
        env_file = ".env"  # Stellen Sie sicher, dass dies auf Ihre .env-Datei verweist
        env_file_encoding = "utf-8"
        case_sensitive = True


settings = Settings()
