from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from .controller import admin_controller as admin
from .controller import auth_controller as auth
from .controller import inventory_read_controller as inventory_read
from .controller import inventory_write_controller as inventory_write
from .core import settings
from .exceptions import (
    DuplicateException,
    InvalidArgumentException,
    NoChangesException,
    NotFoundException,
    UnauthorizedException,
    VersionMissingException,
    VersionConflictException,
)

app = FastAPI(title=settings.PROJECT_NAME)


# Create database tables
# @app.on_event("startup")
# async def startup():
#     async with engine.begin() as conn:
#         await conn.run_sync(Base.metadata.create_all)


# TODO loggger cleanup
# TODO exceptionabfangen wenn die vom product service kommen


app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000"],
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
async def not_found_exception_handler(request: Request, exc: NotFoundException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )

@app.exception_handler(DuplicateException)
async def duplicate_exception_handler(request: Request, exc: DuplicateException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "message": exc.detail,
            "status_code": exc.status_code,
            "_links": {"duplicate": {"href": exc.duplicate_link}},
        },
    )

@app.exception_handler(NoChangesException)
async def no_changes_exception_handler(request: Request, exc: NoChangesException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )

@app.exception_handler(UnauthorizedException)
async def unauthorized_exception_handler(request: Request, exc: UnauthorizedException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )

@app.exception_handler(InvalidArgumentException)
async def invalid_exception_handler(request: Request, exc: InvalidArgumentException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )

@app.exception_handler(VersionMissingException)
async def version_missing_exception_handler(
    request: Request, exc: VersionMissingException
):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )


@app.exception_handler(VersionConflictException)
async def version_conflict_exception_handler(
    request: Request, exc: VersionConflictException
):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )
