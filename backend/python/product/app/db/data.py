from .mongo import db
from ..models import Product, ProductCategoryType
from ..schemas import ProductSchema
from uuid import UUID
from bson import Binary
from ..core import Logger, settings

logger = Logger(__name__)


def generate_uuid(index):
    return UUID(f"70000000-0000-0000-0000-{index:012d}")


example_products = [
    ProductSchema(
        id=generate_uuid(0),
        version=0,
        name="Apple iPhone 14",
        brand="Apple",
        price=999.99,
        description="Neuestes iPhone mit fortschrittlicher Kamera und Display.",
        category=ProductCategoryType.ELECTRONICS,
    ),
    ProductSchema(
        id=generate_uuid(1),
        version=0,
        name="Bio-Äpfel",
        brand="Bauern's Beste",
        price=3.99,
        description="Frische Bio-Äpfel von lokalen Bauernhöfen.",
        category=ProductCategoryType.FRUIT_AND_VEGETABLES,
    ),
    ProductSchema(
        id=generate_uuid(2),
        version=0,
        name="Ledersofa",
        brand="Komfort Leben",
        price=549.99,
        description="Luxuriöses Ledersofa mit Liegefunktion.",
        category=ProductCategoryType.FURNITURE,
    ),
    ProductSchema(
        id=generate_uuid(3),
        version=0,
        name="Herren Freizeitjacke",
        brand="ModeFirma",
        price=79.99,
        description="Stilvolle und bequeme Jacke für alle Jahreszeiten.",
        category=ProductCategoryType.CLOTHING,
    ),
    ProductSchema(
        id=generate_uuid(4),
        version=0,
        name="Lego Star Wars Set",
        brand="LEGO",
        price=59.99,
        description="Bauen Sie Ihr eigenes Star Wars Raumschiff mit diesem lustigen Lego-Set.",
        category=ProductCategoryType.TOYS,
    ),
]


async def insert_example_data():
    collection = db["products"]

    logger.info("Bereinige Produktdatenbank")
    await collection.delete_many({})
    logger.success("Produktsammlung bereinigt")

    for product in example_products:
        try:
            product_dict = product.model_dump(by_alias=True)
            product_dict["_id"] = Binary.from_uuid(product_dict["id"])
            product_dict["category"] = product_dict["category"].name
            del product_dict["id"]

            result = await collection.insert_one(product_dict)
            logger.success(
                f"Produkt eingefügt: {product.name}"
            )
        except Exception as e:
            logger.error(f"Fehler beim Einfügen des Produkts {product.name}: {str(e)}")

    inserted_count = await collection.count_documents({})
    logger.success(f"Insgesamt {inserted_count} Beispielprodukte eingefügt")
