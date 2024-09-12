from pydantic import BaseModel
from typing import Optional
from uuid import UUID

from ..models import InventoryStatusType


# class InventoryStatusType(str, enum.Enum):
#     DISCONTINUED = "D"
#     AVAILABLE = "A"
#     RESERVED = "R"
#     OUT_OF_STOCK = "O"


class InventoryBase(BaseModel):
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str


class InventoryCreate(InventoryBase):
    pass


class InventoryCreateResponse(BaseModel):
    id: UUID


class InventoryUpdate(BaseModel):
    sku_code: Optional[str]
    quantity: Optional[int]
    unit_price: Optional[float]
    status: Optional[InventoryStatusType]
    product_id: str


class InventoryResponse(InventoryBase):
    id: UUID


class InventoryModel(InventoryBase):
    id: UUID



class InventoryRead(InventoryBase):
    id: UUID

    class Config:
        orm_mode = True
