from uuid import UUID

from fastapi import Depends

from ..core import Logger
from ..exception import DuplicateException, VersionConflictException, NoChangesDetectedException
from ..repository import ProductRepository
from ..schemas import ProductCreateSchema, ProductUpdateModel, SearchCriteria
from ..service import ProductReadService

logger = Logger(__name__)

# TODO: implement duplicate key error


class ProductWriteService:
    def __init__(
        self,
        product_repository: ProductRepository = Depends(ProductRepository),
        product_read_service: ProductReadService = Depends(ProductReadService),
    ):
        self.product_repository = product_repository
        self.product_read_service = product_read_service

    async def create_product(self, product: ProductCreateSchema) -> UUID:
        logger.info("Erstelle neues Produkt: {}", product)
        await self.check_duplicate(product.name, product.brand)
        product.category = product.category.name
        product_id = await self.product_repository.create(product)
        logger.success("Produkt erfolgreich erstellt mit ID: {}", product_id)
        return product_id

# TODO namen entfernen
    async def update(
        self, product_id: UUID, product: ProductUpdateModel, version: int
    ) -> bool:
        logger.info("Aktualisiere Produkt mit ID: {}", product_id)

        product_db = await self.product_read_service.find_by_id(product_id)
        if not product_db:
            logger.warning("Produkt nicht gefunden")
            return False
        if product_db.version != version:
            logger.error("update: Konflikt bei den Versionen")
            raise VersionConflictException(product_id, product_db.version, version)
        
        updated_product = ProductUpdateModel(
            name=product.name if product.name is not None else product_db.name,
            description=product.description if product.description is not None else product_db.description,
            price=product.price if product.price is not None else product_db.price,
        )
        
        if product_db.name == updated_product.name and \
           product_db.description == updated_product.description and \
           product_db.price == updated_product.price:
            raise NoChangesDetectedException()
        
        await self.check_duplicate(updated_product.name, product_db.brand, product_id)
        
        updated = await self.product_repository.update(product_id, updated_product, version)
        if not updated:
            logger.warning("Aktualisierung fehlgeschlagen")
        return updated

    async def delete_product(self, product_id: UUID, version: int) -> bool:
        logger.info("Lösche Produkt mit ID: {}", product_id)
        product_db = await self.product_read_service.find_by_id(product_id)
        if not product_db:
            logger.warning("Produkt nicht gefunden")
            return False
        if product_db.version != version:
            logger.error("Löschen: Konflikt bei den Versionen")
            raise VersionConflictException(product_id, product_db.version, version)
        deleted = await self.product_repository.delete(product_id)
        if deleted:
            logger.success("Produkt erfolgreich gelöscht")
        return deleted

    async def check_duplicate(
        self, name: str, brand: str, exclude_id: UUID = None
    ) -> None:
        search_criteria = SearchCriteria(name=name, brand=brand)
        products = await self.product_read_service.find_all(search_criteria)
        if not products:
            return None
        if exclude_id:
            duplicate_products = [product for product in products if product.id != exclude_id]
        else:
            duplicate_products = products
        if duplicate_products:
            logger.error(
                'Das Produkt mit dem Namen "{}" der Marke "{}" existiert bereits.',
                name,
                brand,
            )
            raise DuplicateException(name, brand)


# product_version = product.version
#         if product_version != version:
#             raise VersionConflictException(product_id, product_version, version)
