from typing import Optional
from pydantic import BaseModel
from app.models.product import (
    ProductCategoryType,
) 

class SearchCriteria(BaseModel):
    name: Optional[str] = None
    brand: Optional[str] = None
    category: Optional[ProductCategoryType] = None
    min_price: Optional[float] = None
    max_price: Optional[float] = None
