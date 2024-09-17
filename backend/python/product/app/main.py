from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from .exception import DuplicateKeyError, NotFoundException, UnauthorizedError
from .controller import (
    auth_router as auth,
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


@app.exception_handler(DuplicateKeyError)
async def duplicate_key_error_handler(request: Request, exc: DuplicateKeyError):
    return JSONResponse(status_code=400, content={"detail": str(exc)})


@app.exception_handler(NotFoundException)
async def not_found_exception_handler(request, exc):
    return JSONResponse(
        status_code=exc.status_code,
        content={"message": exc.detail},
    )


@app.exception_handler(UnauthorizedError)
async def unauthorized_error_handler(request: Request, exc: UnauthorizedError):
    return JSONResponse(status_code=401, content={"detail": str(exc)})
