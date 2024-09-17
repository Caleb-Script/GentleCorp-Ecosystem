from uuid import UUID
from fastapi import Depends

from ..exception import DuplicateKeyError
from ..repository import ProductRepository
from ..schemas import ProductSchema
from ..core import Logger

logger = Logger(__name__)


class ProductWriteService:
    def __init__(self, product_repository: ProductRepository = Depends(ProductRepository)):
        self.product_repository = product_repository

    async def create_product(self, product: ProductSchema) -> UUID:
        logger.info("Erstelle neues Produkt: {}", product)
        try:
            product_id = await self.product_repository.create(product)
            logger.success("Produkt erfolgreich erstellt mit ID: {}", product_id)
            return product_id
        except DuplicateKeyError as e:
            logger.error("Fehler beim Erstellen des Produkts: {}", str(e))
            raise

    async def delete_product(self, product_id: UUID) -> bool:
        logger.info("Lösche Produkt mit ID: {}", product_id)
        deleted = await self.product_repository.delete(product_id)
        if deleted:
            logger.success("Produkt erfolgreich gelöscht")
        else:
            logger.warning("Produkt nicht gefunden")
        return deleted
