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
    name: str
    brand: str
    price: float
    description: str
    category: ProductCategoryType

    @classmethod
    def from_mongo(cls, data: dict):
        if data.get("category"):
            data["category"] = ProductCategoryType[data["category"]]
        return cls(**data)
