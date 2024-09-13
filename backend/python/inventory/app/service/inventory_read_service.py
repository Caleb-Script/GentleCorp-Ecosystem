from typing import List, Optional

from fastapi import Depends

from ..models import Inventory
from ..repository import InventoryRepository
from ..schemas import InventoryModel, InventoryBase


class InventoryReadService:

    def __init__(self, repository: InventoryRepository = Depends(InventoryRepository)):
        self.repository = repository

    async def find_by_id(self, id: str) -> Optional[InventoryModel]:
        inventory = await self.repository.get_inventory_by_id(id)
        if inventory:
            # SQLAlchemy -> Pydantic conversion
            return InventoryModel.from_orm(inventory)
        return None

    async def find(self) -> List[InventoryBase]:
        inventories = await self.repository.list_inventory()
        # Convert SQLAlchemy models to Pydantic
        return [InventoryBase.from_orm(inventory) for inventory in inventories]
