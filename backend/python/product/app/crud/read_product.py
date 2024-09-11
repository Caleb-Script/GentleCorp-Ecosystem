from typing import List, Optional

from app.db.mongo import db
from app.models.product import ProductCategoryType
from app.schemas.product import ProductSchema as ProductSchema
from bson import ObjectId

from app.schemas.search_criteria import SearchCriteria


# Helper function to serialize MongoDB ObjectId to string
def serialize_product(product):
    if isinstance(product, dict):
        if "_id" in product:
            product["_id"] = str(product["_id"])  # Convert ObjectId to string
    return product


# Retrieve a single product by ID
def find_by_id(product_id: str) -> Optional[ProductSchema]:
    try:
        product = db.products.find_one({"_id": ObjectId(product_id)})
        if product:
            serialized_product = serialize_product(product)
            return ProductSchema(
                **serialized_product
            )  # Ensure it matches the Pydantic model
    except Exception as e:
        # Log or handle error as appropriate
        print(f"Error retrieving product: {e}")
    return None


def find_products(search_criteria: SearchCriteria) -> List[ProductSchema]:
    try:
        query = {}

        # Name-Filter: Fallunabhängige, teilweise Übereinstimmung
        if search_criteria.name:
            query["name"] = {
                "$regex": search_criteria.name,
                "$options": "i",
            }  # e.g., "phon" or "16"

        # Brand-Filter: Fallunabhängige, teilweise Übereinstimmung
        if search_criteria.brand:
            query["brand"] = {"$regex": search_criteria.brand, "$options": "i"}

        # Kategorie-Filter: Enum-Wert in String umwandeln
        if search_criteria.category:
            query["category"] = (
                search_criteria.category.value
            )  # Enum wird in String umgewandelt

        # Preis-Filter
        if search_criteria.min_price is not None:
            query["price"] = {"$gte": search_criteria.min_price}
        if search_criteria.max_price is not None:
            query.setdefault("price", {})["$lte"] = search_criteria.max_price

        # Produkte aus der Datenbank abrufen, basierend auf der Abfrage
        products = list(db.products.find(query)) if query else list(db.products.find())

        # MongoDB-Dokumente in ProductSchema-Objekte umwandeln
        return [ProductSchema(**serialize_product(product)) for product in products]

    except Exception as e:
        # Fehlerbehandlung (kann Logging hinzufügen)
        print(f"Error retrieving products: {e}")
        return []
