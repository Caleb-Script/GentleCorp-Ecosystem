from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from app.exception.exception import DuplicateKeyError, NotFoundError
from app.routers import auth_router
from .routers import product_router

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=['http://localhost:3000'],
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/")
def read_root():
    return {"Hello": "World"}

app.include_router(product_router.router, prefix="/products", tags=["products"])
app.include_router(auth_router.router, prefix="/auth", tags=["auth"])


@app.exception_handler(DuplicateKeyError)
async def duplicate_key_error_handler(request: Request, exc: DuplicateKeyError):
    return JSONResponse(status_code=400, content={"detail": str(exc)})


@app.exception_handler(NotFoundError)
async def not_found_error_handler(request: Request, exc: NotFoundError):
    return JSONResponse(status_code=404, content={"detail": str(exc)})
