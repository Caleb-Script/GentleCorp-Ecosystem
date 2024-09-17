from uuid import UUID
from pydantic import BaseModel, Field
from enum import Enum


class ProductCategoryType(str, Enum):
    ELECTRONICS = "ELECTRONICS"
    FRUIT_AND_VEGETABLES = "FRUIT_AND_VEGETABLES"
    FURNITURE = "FURNITURE"
    CLOTHING = "CLOTHING"
    TOYS = "TOYS"


class Product(BaseModel):
    id: UUID = Field(...)
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

    class Config:
        json_encoders = {UUID: lambda v: str(v)}
