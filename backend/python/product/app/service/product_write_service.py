from uuid import UUID
from fastapi import Depends

from ..service import ProductReadService
from ..exception import DuplicateException
from ..repository import ProductRepository
from ..schemas import ProductModel, ProductUpdateModel, ProductSchema, SearchCriteria
from ..core import Logger

logger = Logger(__name__)

# TODO: implement duplicate key error

class ProductWriteService:
    def __init__(self, product_repository: ProductRepository = Depends(ProductRepository), product_read_service: ProductReadService = Depends(ProductReadService)):
        self.product_repository = product_repository
        self.product_read_service = product_read_service

    async def create_product(self, product: ProductModel) -> UUID:
        logger.info("Erstelle neues Produkt: {}", product)
        if await self.check_duplicate(product.name, product.brand):
            logger.error(
                'The Product with name "{}" of the brand "{}" already exists.',product.name, product.brand
            )
            raise DuplicateException(
                name=product.name,
                brand=product.brand,
            )
        product.category = product.category.name
        product_id = await self.product_repository.create(product)
        logger.success("Produkt erfolgreich erstellt mit ID: {}", product_id)
        return product_id

    async def update_product(self, product_id: UUID, product: ProductUpdateModel) -> bool:
        logger.info("Aktualisiere Produkt mit ID: {}", product_id)
        try:
            product_db = await self.product_read_service.find_by_id(product_id)
            if not product_db:
                logger.warning("Produkt nicht gefunden")
                return False

            updated_product = ProductUpdateModel(
                name=product.name if product.name is not None else product_db.name,
                description=product.description if product.description is not None else product_db.description,
                price=product.price if product.price is not None else product_db.price,
            )

            updated = await self.product_repository.update(product_id, updated_product)
            if updated:
                logger.success("Produkt erfolgreich aktualisiert")
            else:
                logger.warning("Keine Änderungen vorgenommen")
            return updated
        except DuplicateKeyError as e:
            logger.error("Fehler beim Aktualisieren des Produkts: {}", str(e))
            raise

    async def delete_product(self, product_id: UUID) -> bool:
        logger.info("Lösche Produkt mit ID: {}", product_id)
        deleted = await self.product_repository.delete(product_id)
        if deleted:
            logger.success("Produkt erfolgreich gelöscht")
        else:
            logger.warning("Produkt nicht gefunden")
        return deleted

    async def check_duplicate(
        self, name: str, brand: str, exclude_id: UUID = None
    ) -> bool:
        search_criteria = SearchCriteria(name=name, brand=brand)
        products = await self.product_read_service.find_all(
            search_criteria
        )
        if not products:
            return False
        if exclude_id:
            return any(product.id != exclude_id for product in products)
        return True
