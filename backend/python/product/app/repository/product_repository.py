from uuid import UUID

from bson import Binary
from ..schemas import ProductSchema, SearchCriteria
from ..db.mongo import get_database


class ProductRepository:
    from bson import Binary


class ProductRepository:
    async def find_by_id(self, product_id: UUID) -> ProductSchema:
        db = await get_database()
        product = await db.products.find_one({"_id": Binary.from_uuid(product_id)})
        if product:
            return ProductSchema(**product)
        return None

    async def find_all(self, search_params: SearchCriteria) -> list[dict]:
        db = await get_database()
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

        cursor = db.products.find(query)
        products = await cursor.to_list(length=None)
        return products
