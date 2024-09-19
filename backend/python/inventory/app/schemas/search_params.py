from typing import Optional
from pydantic import BaseModel, Field, ConfigDict
from ..models import InventoryStatusType


class SearchParams(BaseModel):
    sku_code: Optional[str] = None
    min_quantity: Optional[int] = Field(None, ge=0)
    max_quantity: Optional[int] = Field(None, ge=0)
    min_price: Optional[float] = Field(None, ge=0)
    max_price: Optional[float] = Field(None, ge=0)
    status: Optional[InventoryStatusType] = None
    product_id: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)

    def __repr__(self):
        return (
            f"<SearchParams(sku_code={self.sku_code}, min_quantity={self.min_quantity}, "
            f"max_quantity={self.max_quantity}, min_price={self.min_price}, "
            f"max_price={self.max_price}, status={self.status}, product_id={self.product_id})>"
        )
