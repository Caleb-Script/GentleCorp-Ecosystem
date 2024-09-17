from uuid import UUID
from pydantic import BaseModel, Field
from enum import Enum


class ProductCategoryType(str, Enum):
    ELECTRONICS = "E"
    FRUIT_AND_VEGETABLES = "FAV"
    FURNITURE = "F"
    CLOTHING = "C"
    TOYS = "T"


class Product(BaseModel):
    id: UUID = Field(...)
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

    class Config:
        json_encoders = {UUID: lambda v: str(v)}
