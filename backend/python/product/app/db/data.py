from .mongo import db
from ..models.product import Product, ProductCategoryType
from pymongo import errors


# Example Data
example_products = [
    Product(
        name="Apple iPhone 14",
        brand="Apple",
        price=999.99,
        description="Latest iPhone with advanced camera and display.",
        category=ProductCategoryType.ELECTRONICS,
    ),
    Product(
        name="Organic Apples",
        brand="Farmer's Best",
        price=3.99,
        description="Fresh organic apples from local farms.",
        category=ProductCategoryType.FRUIT_AND_VEGETABLES,
    ),
    Product(
        name="Leather Sofa",
        brand="Comfort Living",
        price=549.99,
        description="Luxurious leather sofa with recliner feature.",
        category=ProductCategoryType.FURNITURE,
    ),
    Product(
        name="Men's Casual Jacket",
        brand="FashionCo",
        price=79.99,
        description="Stylish and comfortable jacket for all seasons.",
        category=ProductCategoryType.CLOTHING,
    ),
    Product(
        name="Lego Star Wars Set",
        brand="LEGO",
        price=59.99,
        description="Build your own Star Wars spaceship with this fun Lego set.",
        category=ProductCategoryType.TOYS,
    ),
]


def insert_example_data():
    collection = db["products"]

    # Ensure that the 'name' field is unique by creating an index
    collection.create_index("name", unique=True)

    for product in example_products:
        try:
            # Convert Pydantic model to dictionary
            product_dict = product.model_dump(by_alias=True)

            # Convert the enum value to a string before inserting it
            product_dict["category"] = product_dict["category"].value

            # Insert the product into the collection
            collection.insert_one(product_dict)
        except errors.DuplicateKeyError:
            print(f"Product {product.name} already exists in the database.")
