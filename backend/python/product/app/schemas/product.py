from pydantic import BaseModel
from ..models.product import ProductCategoryType

class ProductSchema(BaseModel):
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

