from typing import List
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.loggin import custom_logger
from ..models import Inventory
from ..schemas import InventoryModel, InventoryRead, InventoryCreate, InventoryCreateResponse
from app.crud.inventory_crud import get_inventory, get_inventories, create_inventory
from ..db import get_session

router = APIRouter()
logger = custom_logger(__name__)
# logger = custom_logger(color="yellow")

@router.get("/skuCode/{sku_code}")
async def get_by_sku_coder(sku_code: str):
    logger.debug("getBySkuCodeAsd: {}", sku_code)
    logger.error("getBySkuCodeAsd: {}", sku_code)
    logger.success("getBySkuCodeAsd: {}", sku_code)
    return sku_code


@router.get("/", response_model=List[InventoryRead])
async def read_inventories(
    skip: int = 0, limit: int = 10, db: AsyncSession = Depends(get_session)
):
    inventories = await get_inventories(db)
    return inventories


@router.get("/id/{inventory_id}", response_model=InventoryModel)
async def read_inventory(inventory_id: str, db: AsyncSession = Depends(get_session)):
    inventory = await get_inventory(db, inventory_id)
    if inventory is None:
        raise HTTPException(status_code=404, detail="Inventory not found")
    return inventory


@router.post("/", response_model=InventoryCreateResponse)
async def create_inventory_item(
    inventory: InventoryCreate, db: AsyncSession = Depends(get_session)
):
    # Erstellen des Inventory-Objekts mit den korrekten Attributen
    db_inventory = Inventory(
        sku_code=inventory.sku_code,
        quantity=inventory.quantity,
        unit_price=inventory.unit_price,
        status=inventory.status.name,
        product_id=inventory.product_id,
    )
    # Erstellen des Inventars in der Datenbank
    created_inventory = await create_inventory(db, db_inventory)

    if not created_inventory:
        raise HTTPException(status_code=400, detail="Inventory creation failed")

    return InventoryCreateResponse(id=created_inventory)


# router = APIRouter()


# def get_db():
#     db = SessionLocal()
#     try:
#         yield db
#     finally:
#         db.close()


# @router.get("/{inventory_id}", response_model=InventoryResponse)
# def read_inventory(inventory_id: int, db: Session = Depends(get_db)):
#     service = InventoryReadService(db)
#     inventory = service.get_inventory(inventory_id)
#     if inventory is None:
#         raise HTTPException(status_code=404, detail="Inventory not found")
#     return inventory


# @router.post("/", response_model=InventoryResponse)
# def create_inventory(inventory: InventoryCreate, db: Session = Depends(get_db)):
#     service = InventoryWriteService(db)
#     return service.create_inventory(inventory.dict())


# @router.get("/{inventory_id}", response_model=InventoryResponse)
# def read_inventory(inventory_id: int, db: Session = Depends(get_db)):
#     service = InventoryReadService(db)
#     inventory = service.get_inventory(inventory_id)
#     if inventory is None:
#         raise HTTPException(status_code=404, detail="Inventory not found")
#     return inventory


# @router.put("/{inventory_id}", response_model=InventoryResponse)
# def update_inventory(
#     inventory_id: int, inventory: InventoryUpdate, db: Session = Depends(get_db)
# ):
#     service = InventoryWriteService(db)
#     updated_inventory = service.update_inventory(inventory_id, inventory.dict())
#     if updated_inventory is None:
#         raise HTTPException(status_code=404, detail="Inventory not found")
#     return updated_inventory


# @router.delete("/{inventory_id}", response_model=InventoryResponse)
# def delete_inventory(inventory_id: int, db: Session = Depends(get_db)):
#     service = InventoryWriteService(db)
#     deleted_inventory = service.delete_inventory(inventory_id)
#     if deleted_inventory is None:
#         raise HTTPException(status_code=404, detail="Inventory not found")
#     return deleted_inventory
