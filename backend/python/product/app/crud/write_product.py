from bson import ObjectId
from app.db.mongo import db
from app.models.product import Product

# Create a product in MongoDB
def create_product(product: Product) -> str:
    product_dict = product.dict()

    # Convert enum to its value (i.e., string)
    if product.category:
        product_dict['category'] = product.category.value

    # Insert into MongoDB
    result = db.products.insert_one(product_dict)
    return str(result.inserted_id)

def delete_product(product_id: str) -> bool:
    try:
        result = db.products.delete_one({"_id": ObjectId(product_id)})
        return result.deleted_count > 0
    except Exception as e:
        print(f"Error deleting product: {e}")
        return False


