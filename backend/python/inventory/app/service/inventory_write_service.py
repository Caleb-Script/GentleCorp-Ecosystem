from typing import Optional

from app.models.inventory import Inventory
from fastapi import Depends

from ..repository import InventoryRepository
from ..schemas import InventoryModel, InventoryCreate, InventoryBase, InventoryUpdate


class InventoryWriteService:

    def __init__(self, repository: InventoryRepository = Depends(InventoryRepository)):
        self.repository = repository

    async def create(self, inventory_create: InventoryModel) -> InventoryBase:
        inventory = Inventory(
            sku_code=inventory_create.sku_code,
            quantity=inventory_create.quantity,
            unit_price=inventory_create.unit_price,
            status=inventory_create.status,
            product_id=inventory_create.product_id,
        )
        created_inventory = await self.repository.create_inventory(inventory)
        return InventoryBase.from_orm(created_inventory)

    async def update(
        self, id: str, inventory_data: InventoryUpdate
    ) -> Optional[Inventory]:
        # Hole das bestehende Inventory-Objekt
        inventory = await self.repository.get_inventory_by_id(id)

        if not inventory:
            return (
                None  # Falls das Inventory-Objekt nicht gefunden wird, gebe None zurück
            )

        # Aktualisiere nur die Attribute, die im Schema bereitgestellt wurden
        if inventory_data.sku_code is not None:
            inventory.sku_code = inventory_data.sku_code
        if inventory_data.quantity is not None:
            inventory.quantity = inventory_data.quantity
        if inventory_data.unit_price is not None:
            inventory.unit_price = inventory_data.unit_price
        if inventory_data.status is not None:
            inventory.status = inventory_data.status

        # Speicher die Änderungen
        await self.repository.update_inventory(id, inventory)

        return inventory

    async def delete(self, id: str) -> Optional[InventoryCreate]:
        return await self.repository.delete_inventory(id)
