from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from .exception import (
    DuplicateException,
    NotFoundException,
    UnauthorizedError,
    InvalidException,
    VersionMissingException,
    VersionConflictException,
)
from .controller import (
    auth_router as auth,
    admin_router as admin,
    product_read_router as product_read,
    product_write_router as product_write,
)
from .db import close_mongo_connection, get_database, insert_example_data
from contextlib import asynccontextmanager
from .core import Logger

logger = Logger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starte Anwendung")
    logger.info("Verbinde mit Datenbank")
    app.mongodb = await get_database()
    logger.success("Datenbankverbindung hergestellt")
    logger.info("Füge Beispieldaten ein")
    await insert_example_data()
    logger.success("Beispieldaten eingefügt")

    yield
    logger.info("Schließe Datenbankverbindung")
    await close_mongo_connection()
    logger.success("Anwendung wird beendet")

app = FastAPI(lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=['http://localhost:3000'],
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def read_root():
    logger.info("Hello World")
    return {"Hello": "World"}

app.include_router(product_read, prefix="/product", tags=["Read products"])
app.include_router(product_write, prefix="/product", tags=["Write products"])
app.include_router(auth, prefix="/auth", tags=["auth"])
app.include_router(admin, prefix="/admin", tags=["admin"])


@app.exception_handler(DuplicateException)
async def duplicate_exception_handler(request: Request, exc: DuplicateException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )


@app.exception_handler(NotFoundException)
async def not_found_exception_handler(request, exc):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )


@app.exception_handler(UnauthorizedError)
async def unauthorized_error_handler(request: Request, exc: UnauthorizedError):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )

@app.exception_handler(InvalidException)
async def invalid_exception_handler(request: Request, exc: InvalidException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail, "status_code": exc.status_code},
    )


@app.exception_handler(VersionMissingException)
async def version_missing_exception_handler(request: Request, exc: VersionMissingException):
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
