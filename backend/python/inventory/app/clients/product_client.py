import os
from fastapi import Depends

from ..security import AuthService
from ..core import settings
from ..repository import ProductRepository

def get_product_base_url():
    schema = settings.PRODUCT_SERVICE_SCHEMA
    host = settings.PRODUCT_SERVICE_HOST
    port = settings.PRODUCT_SERVICE_PORT
    return f"{schema}://{host}:{port}"

class ProductRepositoryDependency:
    def __init__(self):
        self.base_url = get_product_base_url()

    def __call__(self, token: str = Depends(AuthService.get_bearer_token)):
        return ProductRepository(self.base_url, token)

get_product_repository = ProductRepositoryDependency()
