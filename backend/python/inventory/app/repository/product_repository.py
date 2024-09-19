from typing import Optional
import httpx
from fastapi import HTTPException
from uuid import UUID

from ..exceptions import NotFoundException
from ..clients.product import ProductInfo


class ProductRepository:
    def __init__(self, base_url: str, token: str):
        self.base_url = base_url
        self.client = httpx.AsyncClient(base_url=base_url)
        self.token = token

    async def get_by_id(self, id: UUID, version: Optional[str] = None) -> ProductInfo:
        headers = {"Authorization": f"Bearer {self.token}"}
        if version:
            headers["If-None-Match"] = version
        try:
            response = await self.client.get(f"/product/{id}", headers=headers)
            response.raise_for_status()
            product_data = response.json()
            return ProductInfo.to_product_info(product_data)
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 404:
                raise NotFoundException(product_id=id)
            raise HTTPException(status_code=e.response.status_code, detail=str(e))

    async def close(self):
        await self.client.aclose()
