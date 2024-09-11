from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker, declarative_base
from ..core import settings

# Define the base class for declarative models
Base = declarative_base()

# Create the database engine using the URL from settings
engine = create_async_engine(settings.DATABASE_URL, echo=True)

# Create a configured "Session" class
# Create a sessionmaker for async sessions
AsyncSessionLocal = sessionmaker(
    bind=engine, class_=AsyncSession, expire_on_commit=False
)


# Dependency to get the async session
async def get_session():
    async with AsyncSessionLocal() as session:
        yield session
