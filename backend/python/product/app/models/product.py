import enum
from uuid import UUID, uuid4
from pydantic import BaseModel, Field


class ProductCategoryType(enum.Enum):
    ELECTRONICS = "E"
    FRUIT_AND_VEGETABLES = "FAV"
    FURNITURE = "F"
    CLOTHING = "C"
    TOYS = "T"


class Product(BaseModel):
    id: UUID = Field(default_factory=uuid4, alias="_id")
    version: int
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

    class Config:
        populate_by_name = True
        json_encoders = {UUID: lambda v: str(v)}

    @classmethod
    def from_mongo(cls, data: dict):
        if data.get("category"):
            data["category"] = ProductCategoryType[data["category"]]
        return cls(**data)
