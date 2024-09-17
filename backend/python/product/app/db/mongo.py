from motor.motor_asyncio import AsyncIOMotorClient
from ..core import settings

client = AsyncIOMotorClient(settings.DATABASE_URL)
db = client[settings.DATABASE_NAME]


async def get_database():
    return db


async def close_mongo_connection():
    client.close()
