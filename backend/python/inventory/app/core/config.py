import os
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    PROJECT_NAME: str = "Inventory Service"
    DATABASE_URL: str
    KEYCLOAK_SERVER_URL: str
    KEYCLOAK_CLIENT_ID: str
    KEYCLOAK_CLIENT_SECRET: str
    LOG_LEVEL: str
    APP_ENV: str = "development"  # Added if needed
    APP_DEBUG: bool = True

    class Config:
        env_file = ".env"  # Make sure this points to your .env file if you have one
        env_file_encoding = "utf-8"


settings = Settings()
