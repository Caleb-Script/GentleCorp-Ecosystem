from fastapi import Depends
from uuid import UUID, uuid4
from bson import Binary
from ..schemas import SearchCriteria
from ..models import Product
from ..db import get_database
from ..core import Logger

logger = Logger(__name__)

class ProductRepository:
    def __init__(self, db=Depends(get_database)):
        self.db = db

    async def find_by_id(self, product_id: UUID) -> Product | None:
        product = await self.db.products.find_one({"_id": Binary.from_uuid(product_id)})
        if product:
            return Product.from_mongo(product)
        return None

    async def find_all(self, search_params: SearchCriteria) -> list[Product]:
        query = {}

        if search_params.name:
            query["name"] = {"$regex": search_params.name, "$options": "i"}
        if search_params.brand:
            query["brand"] = {"$regex": search_params.brand, "$options": "i"}
        if search_params.category:
            query["category"] = search_params.category
        if search_params.min_price is not None or search_params.max_price is not None:
            query["price"] = {}
            if search_params.min_price is not None:
                query["price"]["$gte"] = search_params.min_price
            if search_params.max_price is not None:
                query["price"]["$lte"] = search_params.max_price

        cursor = self.db.products.find(query)
        products = await cursor.to_list(length=None)
        product_model = [Product.from_mongo(product) for product in products]
        return product_model

    async def update(self, product_id: UUID, product: Product, version: int) -> bool:
        logger.info("Aktualisiere Produkt mit ID: {}", product_id)
        result = await self.db.products.update_one(
            {"_id": Binary.from_uuid(product_id), "version": version},
            {
                "$set": product.model_dump(exclude={"id"}),
                "$inc": {"version": 1}
            }
        )
        if result.modified_count > 0:
            logger.success("Produkt erfolgreich aktualisiert")
            return True
        logger.warning("Produkt nicht gefunden oder keine Änderungen vorgenommen")
        return False

    async def create(self, product: Product) -> UUID:
        logger.info("Erstelle neues Produkt: {}", product)

        product_id = uuid4()
        product_dict = product.model_dump(exclude={"id", "version"})
        product_dict["_id"] = Binary.from_uuid(product_id)
        product_dict["version"] = 0
        await self.db.products.insert_one(product_dict)
        logger.success("Produkt erfolgreich erstellt mit ID: {}", product_id)
        return product_id

    async def delete(self, product_id: UUID) -> bool:
        logger.info("Lösche Produkt mit ID: {}", product_id)
        result = await self.db.products.delete_one({"_id": Binary.from_uuid(product_id)})
        if result.deleted_count > 0:
            logger.success("Produkt erfolgreich gelöscht")
            return True
        logger.warning("Produkt nicht gefunden")
        return False
