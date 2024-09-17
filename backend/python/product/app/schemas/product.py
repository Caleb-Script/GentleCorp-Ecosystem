from uuid import UUID, uuid4
from pydantic import BaseModel, Field
from enum import Enum

from ..models import ProductCategoryType


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
    id: UUID = Field(default_factory=uuid4, alias="_id")



class SearchCriteria(BaseModel):
    name: str | None = None
    brand: str | None = None
    category: ProductCategoryType | None = None
    min_price: float | None = None
    max_price: float | None = None
