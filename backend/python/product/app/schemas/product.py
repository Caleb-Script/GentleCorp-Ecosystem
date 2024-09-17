from uuid import UUID
from pydantic import BaseModel, Field
from enum import Enum


class ProductCategoryType(str, Enum):
    ELECTRONICS = "ELECTRONICS"
    CLOTHING = "CLOTHING"
    BOOKS = "BOOKS"
    TOYS = "TOYS"
    FURNITURE = "FURNITURE"
    FRUIT_AND_VEGETABLES = "FRUIT_AND_VEGETABLES"
    # ... andere Kategorien ...

class ProductModel(BaseModel):
    name: str
    description: str
    price: float
    category: ProductCategoryType
    brand: str

    class Config:
        populate_by_name = True
        json_encoders = {UUID: lambda v: str(v)}


class ProductSchema(ProductModel):
    id: UUID = Field(alias="_id")

    class Config:
        allow_population_by_field_name = True
        json_encoders = {UUID: str}


class SearchCriteria(BaseModel):
    name: str | None = None
    brand: str | None = None
    category: ProductCategoryType | None = None
    min_price: float | None = None
    max_price: float | None = None
