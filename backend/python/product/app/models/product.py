from pydantic import BaseModel
from enum import Enum


class ProductCategoryType(Enum):
    FRUIT_AND_VEGETABLES = "FV"
    ELECTRONICS = "E"
    HOUSEHOLD = "H"
    FURNITURE = "F"
    CLOTHING = "C"
    TOYS = "T"


class Product(BaseModel):
  # productId = str
  name: str
  brand: str
  price: float
  description: str
  category: ProductCategoryType
