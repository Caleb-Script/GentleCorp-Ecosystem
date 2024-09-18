from typing import Optional
from uuid import UUID, uuid4
from pydantic import BaseModel, Field
from enum import Enum

from ..models import ProductCategoryType


class ProductModel(BaseModel):
    name: str
    brand: str
    description: str
    price: float
    category: ProductCategoryType

    class Config:
        populate_by_name = True
        json_encoders = {UUID: lambda v: str(v)}

class ProductUpdateModel(BaseModel):
    name: Optional[str] = None
    description:Optional[str] = None
    price: Optional[float] = None


class ProductSchema(ProductModel):
    id: UUID = Field(...)

    class Config:
        json_encoders = {UUID: lambda v: str(v)}
