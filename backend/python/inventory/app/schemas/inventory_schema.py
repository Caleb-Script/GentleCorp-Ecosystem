from pydantic import BaseModel, ConfigDict
from typing import Optional
from uuid import UUID
from ..models import InventoryStatusType


class InventoryBase(BaseModel):
    id: UUID
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str

    class Config:
        from_attributes = True


class InventoryModel(BaseModel):
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str

    class Config:
        from_attributes = True


class InventoryCreate(InventoryModel):
    pass


class InventoryUpdate(BaseModel):
    sku_code: Optional[str] = None
    quantity: Optional[int]= None
    unit_price: Optional[float]= None
    status: Optional[InventoryStatusType]= None


class InventoryResponse(InventoryBase):
    id: UUID


class InventoryRead(InventoryBase):
    id: UUID

    class Config:
        from_attributes = True
