from pydantic import BaseModel
from ..models.product import ProductCategoryType

class ProductSchema(BaseModel):
    _id: str
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

