from typing import List, Optional
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from sqlalchemy import update, delete

from ..db import get_session
from ..models import Inventory, ReservedProduct


class InventoryRepository:

    def __init__(self, session: AsyncSession = Depends(get_session)):
        self.session = session

    async def get_inventory_by_id(self, id: str) -> Optional[Inventory]:
        result = await self.session.execute(select(Inventory).filter_by(id=id))
        return result.scalars().first()

    async def list_inventory(self) -> List[Inventory]:
        result = await self.session.execute(select(Inventory))
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
                delete(ReservedProduct).where(ReservedProduct.inventory_id == id)
            )

            # Now delete the inventory
            result = await self.session.execute(select(Inventory).filter_by(id=id))
            inventory = result.scalars().first()
            if inventory:
                await self.session.delete(inventory)
                await self.session.commit()
            return inventory
