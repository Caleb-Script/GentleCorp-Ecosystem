import os
from fastapi import Depends

from ..repository import ProductRepository

def get_product_base_url():
    schema = os.getenv("PRODUCT_SERVICE_SCHEMA", "http")
    host = os.getenv("PRODUCT_SERVICE_HOST", "localhost")
    port = os.getenv("PRODUCT_SERVICE_PORT", "8081")
    return f"{schema}://{host}:{port}"


def get_product_repository(base_url: str = Depends(get_product_base_url), token: str = Depends(get_auth_token)):
    return ProductRepository(base_url)
