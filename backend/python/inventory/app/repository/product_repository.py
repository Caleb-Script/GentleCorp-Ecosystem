from typing import Optional
import httpx
from fastapi import HTTPException
from pydantic import BaseModel
from ..core import custom_logger
from ..clients.product import ProductInfo

logger = custom_logger(__name__)



class ProductRepository:
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.client = httpx.AsyncClient(base_url=base_url)
        self.token = token

    async def get_by_id(self, id: str, version: Optional[str] = None) -> ProductInfo:
        headers = {"Authorization": f"Bearer {self.token}"}
        if version:
            headers["If-None-Match"] = version
        try:
            response = await self.client.get(f"/product/{id}", headers=headers)
            response.raise_for_status()
            product_data = response.json()
            logger.debug("get_by_id: product={}", product_data)
            return ProductInfo(name=product_data['name'])
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 404:
                raise HTTPException(status_code=404, detail=f"Product with id {id} not found")
            raise HTTPException(status_code=e.response.status_code, detail=str(e))

    async def close(self):
        await self.client.aclose()
