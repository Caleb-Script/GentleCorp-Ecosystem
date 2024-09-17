from typing import Optional
import httpx
from fastapi import Header


class ProductRepository:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.client = httpx.AsyncClient(base_url=base_url)

    # async def get_by_id(self, account_id: str):
    #     async with httpx.AsyncClient() as client:
    #         headers = {"Authorization": f"Bearer {self.auth_token}"}
    #         response = await client.get(
    #             f"{self.base_url}/account/{account_id}", headers=headers
    #         )

    #         if response.status_code == 404:
    #             raise HTTPException(status_code=404, detail="Account not found")
    #         response.raise_for_status()
    #         return response.json()



    async def get_by_id(
        self, id: str, authorization: str, version: Optional[str] = None
    ):
        headers = {"Authorization": authorization}
        if version:
            headers["If-None-Match"] = version
        response = await self.client.get(f"/product/{id}", headers=headers)
        response.raise_for_status()
        return response.json()

    async def close(self):
        await self.client.aclose()
