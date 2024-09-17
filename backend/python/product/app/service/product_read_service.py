from uuid import UUID

from fastapi import Depends

from ..exception import NotFoundException
from ..repository import ProductRepository
from ..schemas import ProductSchema, SearchCriteria
from ..core import Logger

logger = Logger(__name__)


class ProductReadService:
    def __init__(self, product_repository: ProductRepository = Depends(ProductRepository)):
        self.product_repository = product_repository

    async def find_by_id(self, product_id: UUID) -> ProductSchema:
        logger.info("Finding product by ID: {}", product_id)
        product = await self.product_repository.find_by_id(product_id)
        if product is None:
            raise NotFoundException(id)
        logger.success("Found product: {}", product)
        return product

    async def find_products(self, search_criteria: SearchCriteria) -> list[ProductSchema]:
        logger.debug("Searching products: {}", search_criteria)
        products = await self.product_repository.find_all(search_criteria)
        products_schema = [ProductSchema.model_validate(product) for product in products]
        logger.debug("Found products: {}", products_schema)
        return products_schema
