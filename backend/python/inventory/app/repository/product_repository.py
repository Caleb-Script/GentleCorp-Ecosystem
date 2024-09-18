from typing import Optional
import httpx
from fastapi import HTTPException
from pydantic import BaseModel

class ProductInfo(BaseModel):
    id: str
    name: str
    # Fügen Sie hier weitere Felder hinzu, die Sie vom Product-Service erwarten

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
            return ProductInfo(**response.json())
        except httpx.HTTPStatusError as e:
            if e.response.status_code == 404:
                raise HTTPException(status_code=404, detail=f"Product with id {id} not found")
            raise HTTPException(status_code=e.response.status_code, detail=str(e))

    async def close(self):
        await self.client.aclose()
