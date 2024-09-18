from pydantic import BaseModel, ConfigDict
from typing import List, Optional
from uuid import UUID

from ..models import InventoryStatusType


class InventoryBase(BaseModel):
    id: UUID
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str
    name: str
    
    class Config:
        orm_model = True
        from_attributes = True


class InventoryModel(BaseModel):
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str
    name: str

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<Inventory(sku_code={self.sku_code}, quantity={self.quantity}), >"


class ReservationModel(BaseModel):
    quantity: int

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<ReservationModel(quantity={self.quantity}"


class ReservationDetailModel(ReservationModel):
    username: str

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<ReservationFullModel(quantity={self.quantity}, username={self.username})>"


class ReservationFullModel(ReservationModel):
    inventory_id: UUID

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<ReservationDetailModel(quantity={self.quantity}, inventory_id={self.inventory_id})>"


class InventoryFullModel(InventoryModel):
    reserved_items: Optional[List[ReservationDetailModel]] = None

    def __repr__(self):
        return (
            f"<InventoryFull(sku_code={self.sku_code}, quantity={self.quantity}, unit_price={self.unit_price}, status={self.status}, product_id={self.product_id},  "
            f"reserved_items={self.reserved_items})>"
        )


class InventoryUpdate(BaseModel):
    sku_code: Optional[str] = None
    quantity: Optional[int] = None
    unit_price: Optional[float] = None
    status: Optional[InventoryStatusType] = None
