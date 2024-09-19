from fastapi import FastAPI
from fastapi.responses import JSONResponse

from .exceptions import NotFoundException
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


# TODO loggger cleanup
# TODO exceptionabfangen wenn die vom product service kommen
# TODO skucode generator
# TODO versionierung

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


@app.exception_handler(NotFoundException)
async def not_found_exception_handler(request, exc: NotFoundException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )
