from typing import List, Optional

from fastapi import Depends
from sqlalchemy import UUID, delete, update
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy.orm import joinedload

from ..db import get_session
from ..models import Inventory, ReservedItem
from ..schemas import SearchParams


class InventoryRepository:

    def __init__(self, session: AsyncSession = Depends(get_session)):
        self.session = session

    async def get_inventory_by_id(self, id: str, full: bool) -> Optional[Inventory]:
        query = select(Inventory).filter_by(id=id)

        if full:
            query = query.options(joinedload(Inventory.reserved_items))

        result = await self.session.execute(query)
        return result.scalars().first()

    async def list_inventory(self, search_params: SearchParams) -> List[Inventory]:
        query = select(Inventory)

        # Apply filters based on search_params
        if search_params.sku_code:
            query = query.filter(Inventory.sku_code == search_params.sku_code)
        if search_params.min_quantity is not None:
            query = query.filter(Inventory.quantity >= search_params.min_quantity)
        if search_params.max_quantity is not None:
            query = query.filter(Inventory.quantity <= search_params.max_quantity)
        if search_params.min_price is not None:
            query = query.filter(Inventory.unit_price >= search_params.min_price)
        if search_params.max_price is not None:
            query = query.filter(Inventory.unit_price <= search_params.max_price)
        if search_params.status:
            query = query.filter(Inventory.status == search_params.status)

        result = await self.session.execute(query)
        return result.scalars().all()

    async def create_inventory(self, inventory: Inventory) -> Inventory:
        self.session.add(inventory)
        try:
            await self.session.commit()
        except Exception as e:
            await self.session.rollback()
            # Optionally, log the error
            raise e
        return inventory

    async def update_inventory(
        self, id: str, updated_inventory: Inventory
    ) -> Optional[Inventory]:
        # Hole das bestehende Inventory-Objekt
        result = await self.session.execute(select(Inventory).filter_by(id=id))
        inventory = result.scalars().first()

        if inventory:
            # Übertrage die Änderungen vom aktualisierten Inventory
            for key, value in updated_inventory.__dict__.items():
                if value is not None:
                    setattr(inventory, key, value)
            try:
                await self.session.commit()
                await self.session.refresh(
                    inventory
                )  # Stelle sicher, dass die Änderungen geladen werden
            except Exception as e:
                await self.session.rollback()
                # Optional: Fehler protokollieren
                raise e

        return inventory

    async def delete_inventory(self, id: str) -> Optional[Inventory]:
        async with self.session.begin():
            # Delete dependent reserved products
            await self.session.execute(
                delete(ReservedItem).where(ReservedItem.inventory_id == id)
            )

            # Now delete the inventory
            result = await self.session.execute(select(Inventory).filter_by(id=id))
            inventory = result.scalars().first()
            if inventory:
                await self.session.delete(inventory)
                await self.session.commit()
            return inventory
    


    async def update_reserved_item(self, reserved_item: ReservedItem):
        self.session.add(reserved_item)
        try:
            await self.session.commit()
            await self.session.refresh(reserved_item)
        except Exception as e:
            await self.session.rollback()
            raise e
        return reserved_item
    
    async def get_reserved_items(self, identifier: str) -> List[ReservedItem]:
        query = select(ReservedItem).filter(
            (ReservedItem.inventory_id == identifier) | (ReservedItem.username == identifier)
        )
        result = await self.session.execute(query)
        return result.scalars().all()
    
    async def add_reserved_item(self, reserved_item: ReservedItem):
        self.session.add(reserved_item)
        try:
            await self.session.commit()
            await self.session.refresh(reserved_item)
        except Exception as e:
            await self.session.rollback()
            raise e