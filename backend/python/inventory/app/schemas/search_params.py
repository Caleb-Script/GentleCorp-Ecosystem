from typing import Optional

from pydantic import BaseModel

from ..models import InventoryStatusType


class SearchParams(BaseModel):
    sku_code: Optional[str] = None
    min_quantity: Optional[int] = None
    max_quantity: Optional[int] = None
    min_price: Optional[float] = None
    max_price: Optional[float] = None
    status: Optional[InventoryStatusType] = None
    product_id: Optional[str] = None

    class Config:
        orm_mode = True
