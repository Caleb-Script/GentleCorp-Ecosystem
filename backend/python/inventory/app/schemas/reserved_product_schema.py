from pydantic import BaseModel


class ReservationCreate(BaseModel):
    sku_code: str
    quantity: int
    inventory_id: str
