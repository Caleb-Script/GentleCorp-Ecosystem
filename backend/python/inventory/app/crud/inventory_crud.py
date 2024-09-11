from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.future import select
from app.models.inventory import Inventory


async def get_inventory(
    db: AsyncSession, inventory_id: str
):  # UUID ist typischerweise als str gespeichert
    result = await db.execute(select(Inventory).filter(Inventory.id == inventory_id))
    return result.scalars().first()


async def get_inventories(db: AsyncSession):
    result = await db.execute(select(Inventory))
    return result.scalars().all()


async def create_inventory(db: AsyncSession, inventory: Inventory):
    db.add(inventory)
    await db.commit()
    await db.refresh(inventory)
    return inventory.id
