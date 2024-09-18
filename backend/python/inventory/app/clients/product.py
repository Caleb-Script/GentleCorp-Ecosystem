from pydantic import BaseModel


class ProductInfo(BaseModel):
    name: str
