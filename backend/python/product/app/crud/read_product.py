from bson import ObjectId
from typing import List, Optional
from app.db.mongo import db
from app.schemas.product import ProductSchema as ProductSchema

# Helper function to serialize MongoDB ObjectId to string
def serialize_product(product):
    if isinstance(product, dict):
        if "_id" in product:
            product["_id"] = str(product["_id"])  # Convert ObjectId to string
    return product

# Retrieve a single product by ID
def get_product(product_id: str) -> Optional[ProductSchema]:
    try:
        product = db.products.find_one({"_id": ObjectId(product_id)})
        if product:
            serialized_product = serialize_product(product)
            return ProductSchema(**serialized_product)  # Ensure it matches the Pydantic model
    except Exception as e:
        # Log or handle error as appropriate
        print(f"Error retrieving product: {e}")
    return None

# Retrieve all products
def get_all_products() -> List[ProductSchema]:
    try:
        products = list(db.products.find())
        return [ProductSchema(**serialize_product(product)) for product in products]  # Ensure each product matches the Pydantic model
    except Exception as e:
        # Log or handle error as appropriate
        print(f"Error retrieving products: {e}")
        return []

# Filter products based on various criteria
def filter_products(
    name: Optional[str] = None,
    brand: Optional[str] = None,
    category: Optional[str] = None,
    min_price: Optional[float] = None,
    max_price: Optional[float] = None
) -> List[ProductSchema]:
    try:
        query = {}

        if name:
            query["name"] = {"$regex": name, "$options": "i"}  # Case-insensitive search
        if brand:
            query["brand"] = {"$regex": brand, "$options": "i"}
        if category:
            query["category"] = category
        if min_price is not None:
            query["price"] = {"$gte": min_price}
        if max_price is not None:
            query.setdefault("price", {})["$lte"] = max_price

        products = list(db.products.find(query))
        return [ProductSchema(**serialize_product(product)) for product in products]
    except Exception as e:
        # Log or handle error as appropriate
        print(f"Error filtering products: {e}")
        return []

def delete_product(product_id: str) -> bool:
    try:
        result = db.products.delete_one({"_id": ObjectId(product_id)})
        return result.deleted_count > 0
    except Exception as e:
        print(f"Error deleting product: {e}")
        return False


