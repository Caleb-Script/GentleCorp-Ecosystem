from fastapi import FastAPI
from .db import engine, Base
from fastapi.middleware.cors import CORSMiddleware
from .routers import router as inventory
from .core import settings

app = FastAPI(title=settings.PROJECT_NAME)


# Create database tables
@app.on_event("startup")
async def startup():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)


app.add_middleware(
    CORSMiddleware,
    allow_origins=['http://localhost:3000'],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include the inventory router
app.include_router(inventory, prefix="/inventory", tags=["inventory"])

@app.get("/")
def read_root():
    return {"message": "Welcome to the Inventory Service!"}
