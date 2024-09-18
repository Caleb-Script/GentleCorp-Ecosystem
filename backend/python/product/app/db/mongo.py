import os

print("Environment variables in mongo.py:")
print(f"DATABASE_URL: {os.getenv('DATABASE_URL')}")
print(f"DATABASE_NAME: {os.getenv('DATABASE_NAME')}")

from motor.motor_asyncio import AsyncIOMotorClient
from ..core.settings import settings
print(f"DATABASE_URL: {settings.DATABASE_URL}")
print(f"DATABASE_NAME: {settings.DATABASE_NAME}")

client = AsyncIOMotorClient(settings.DATABASE_URL)
db = client[settings.DATABASE_NAME]


async def get_database():
    return db


async def close_mongo_connection():
    client.close()
