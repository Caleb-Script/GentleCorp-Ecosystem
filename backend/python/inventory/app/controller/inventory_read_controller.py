from typing import List
from fastapi import APIRouter, Depends, HTTPException, status
from ..core import custom_logger
from ..schemas import InventoryBase, InventoryModel
from ..service import AuthService, InventoryReadService

router = APIRouter()
logger = custom_logger(__name__)


# otional mit resevierungen
@router.get("/{id}", response_model=InventoryModel)
async def get_by_id(
    id: str,
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
):
    logger.debug("getById: {}", id)
    inventory = await service.find_by_id(id)
    if not inventory:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Inventory not found"
        )
    return inventory


@router.get("/", response_model=List[InventoryBase])
async def list_inventory(service: InventoryReadService = Depends(InventoryReadService)):
    return await service.find()


# @router.get("/", response_model=List[InventoryRead])
# async def read_inventories(
#     skip: int = 0, limit: int = 10, db: AsyncSession = Depends(get_session)
# ):
#     inventories = await get_inventories(db)
#     return inventories
