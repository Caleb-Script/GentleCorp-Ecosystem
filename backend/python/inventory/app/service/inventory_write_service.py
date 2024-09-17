from typing import Optional
from uuid import UUID

from app.models.inventory import Inventory
from fastapi import Depends

from ..service import InventoryReadService
from ..repository import InventoryRepository
from ..schemas import InventoryModel, InventoryBase, InventoryUpdate, ReservationModel
from ..mapper import InventoryMapper
from ..core import custom_logger
from ..exceptions import UnzureichenderBestandFehler
from app.models import ReservedItem
from app.exceptions import UnzureichenderBestandFehler, NotFoundException

logger = custom_logger(__name__)


class InventoryWriteService:

    def __init__(
        self,
        repository: InventoryRepository = Depends(InventoryRepository),
        inventoryReadService: InventoryReadService = Depends(InventoryReadService),
        inventoryMapper: InventoryMapper = Depends(InventoryMapper),
    ):
        self.repository = repository
        self.inventoryReadService = inventoryReadService
        self.inventoryMapper = inventoryMapper

    async def create(self, inventory_create: InventoryModel) -> InventoryBase:
        logger.debug("create: inventory_create={}", inventory_create)
        inventory = self.inventoryMapper.to_inventory(inventory_create)
        created_inventory = await self.repository.create_inventory(inventory)
        logger.debug("create: created_inventory={}", created_inventory)
        return InventoryBase.model_validate(created_inventory)

    async def update(self, id: str, inventory_data: InventoryUpdate) -> InventoryBase:

        inventory = await self.inventoryReadService.find_by_id(id, True)

        update_data = {
            field: value
            for field, value in inventory_data.model_dump(exclude_unset=True).items()
        }

        for field, value in update_data.items():
            setattr(inventory, field, value)

        await self.repository.update_inventory(id, inventory)
        return InventoryBase.model_validate(inventory)

    async def delete(self, id: str) -> Optional[InventoryModel]:
        return await self.repository.delete_inventory(id)

    async def reseveItem(
        self, id: str, item: ReservationModel, username: str
    ) -> InventoryBase:
        inventory = await self.inventoryReadService.find_by_id(id, True)

        if not inventory:
            raise NotFoundException(id=id)

        übrigeQuantity = await self.inventoryReadService.calculateQantity(inventory)
        logger.debug("Übrige Quantity: {}", übrigeQuantity)

        if übrigeQuantity < item.quantity:
            raise UnzureichenderBestandFehler(inventory.id)
        
        try:
            reserved_item = await self.inventoryReadService.find_reserved_item_by_username(username, id)
        except NotFoundException:
            reserved_item = None

        if reserved_item:
            # Aktualisiere die vorhandene Reservierung
            reserved_item.quantity += item.quantity
            await self.repository.update_reserved_item(reserved_item)
        else:
            # Erstelle eine neue Reservierung
            new_reserved_item = ReservedItem(
                quantity=item.quantity, username=username, inventory_id=UUID(id)
            )
            await self.repository.add_reserved_item(new_reserved_item)
            inventory.reserved_items.append(new_reserved_item)

        # # Aktualisiere die verfügbare Menge im Inventar
        # inventory.quantity -= item.quantity

        # Speichere die Änderungen
        updated_inventory = await self.repository.update_inventory(id, inventory)

        return InventoryBase.model_validate(updated_inventory)
