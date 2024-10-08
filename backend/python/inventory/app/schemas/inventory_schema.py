from pydantic import BaseModel, ConfigDict
from typing import List, Optional
from uuid import UUID

from ..models import InventoryStatusType

class InventoryCreateModel(BaseModel):
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<Inventory(sku_code={self.sku_code}, quantity={self.quantity}), >"

class InventoryBase(BaseModel):
    id: UUID
    version: int
    sku_code: str
    quantity: int
    unit_price: float
    status: InventoryStatusType
    product_id: str
    name: str
    brand: str

    class Config:
        orm_model = True
        from_attributes = True


class InventoryModel(InventoryCreateModel):
    version: int
    sku_code: str
    name: str
    brand: str

class ReservationCreateModel(BaseModel):
    quantity: int

    class Config:
        from_attributes = True

    def __repr__(self):
        return f"<ReservationModel(quantity={self.quantity}"


class ReservationModel(ReservationCreateModel):
    version: int
    quantity: int


class ReservationDetailModel(ReservationModel):
    username: str
    id: UUID

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
    quantity: Optional[int] = None
    unit_price: Optional[float] = None
    status: Optional[InventoryStatusType] = None
