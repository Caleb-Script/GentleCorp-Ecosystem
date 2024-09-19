from enum import Enum
from typing import Optional
from uuid import UUID, uuid4

from pydantic import BaseModel, Field

from ..models import ProductCategoryType


class ProductCreateSchema(BaseModel):
    name: str
    brand: str
    description: str
    price: float
    category: ProductCategoryType

    class Config:
        populate_by_name = True
        json_encoders = {UUID: lambda v: str(v)}


class ProductModel(ProductCreateSchema):
    version: int


class ProductSchema(ProductModel):
    id: UUID = Field(...)

# TODO namen entfernen
class ProductUpdateModel(BaseModel):
    name: Optional[str] = None
    description: Optional[str] = None
    price: Optional[float] = None
