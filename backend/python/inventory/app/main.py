from fastapi import FastAPI
from .db import engine, Base
from fastapi.middleware.cors import CORSMiddleware
from .core import settings
from .controller import inventory_read_controller as inventory_read, inventory_write_controller as inventory_write, admin_controller as admin, auth_controller as auth

app = FastAPI(title=settings.PROJECT_NAME)


# Create database tables
# @app.on_event("startup")
# async def startup():
#     async with engine.begin() as conn:
#         await conn.run_sync(Base.metadata.create_all)


app.add_middleware(
    CORSMiddleware,
    allow_origins=['http://localhost:3000'],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(inventory_read, prefix="/inventory", tags=["Inventory Read"])
app.include_router(inventory_write, prefix="/inventory", tags=["Inventory Write"])
app.include_router(admin, prefix="/admin", tags=["admin"])
app.include_router(auth, prefix="/auth", tags=["Auth"])

@app.get("/")
def read_root():
    return {"message": "Welcome to the Inventory Service!"}
