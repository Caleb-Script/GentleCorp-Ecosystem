from uuid import UUID

from fastapi import Depends

from ..core import Logger
from ..exception import NotFoundException
from ..repository import ProductRepository
from ..schemas import ProductSchema, SearchCriteria

logger = Logger(__name__)


class ProductReadService:
    def __init__(
        self, product_repository: ProductRepository = Depends(ProductRepository)
    ):
        self.product_repository = product_repository

    async def find_by_id(self, product_id: UUID) -> ProductSchema:
        logger.info("Finding product by ID: {}", product_id)
        product = await self.product_repository.find_by_id(product_id)
        if product is None:
            raise NotFoundException(product_id)
        logger.success("Found product: {}", product)
        return product

    async def find_all(self, search_criteria: SearchCriteria) -> list[ProductSchema]:
        logger.debug("Searching products: {}", search_criteria)
        products = await self.product_repository.find_all(search_criteria)
        return products
