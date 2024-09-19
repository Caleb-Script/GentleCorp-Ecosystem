from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from uuid import UUID

from app.schemas.inventory_schema import ReservationDetailModel
from app.security.user import Role

from ..security import User
from ..core import custom_logger
from ..schemas import InventoryBase, InventoryModel, SearchParams, InventoryFullModel, ReservationFullModel, ReservationModel
from ..service import AuthService, InventoryReadService
from ..exceptions import NotFoundException

router = APIRouter()
logger = custom_logger(__name__)

@router.get("/", response_model=List[InventoryBase])
async def list_inventory(
    search_params: SearchParams = Depends(),
    service: InventoryReadService = Depends(InventoryReadService),
):
    inventories = await service.find(search_params)
    if not inventories:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="No inventories found")
    return inventories

@router.get("/reserve", response_model=List[ReservationFullModel])
async def get_reserved_items_by_username(
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    logger.debug("getReservedItemsByUsername: user={}", user)
    reserved_items = await service.find_by_username(username)
    if not reserved_items:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="No reserved items found")
    reserved_items_models = [ReservationFullModel.model_validate(item) for item in reserved_items]
    logger.success("Found reserved items: {}", reserved_items_models)
    return reserved_items_models

@router.get("/reserved/{inventory_id}", response_model=List[ReservationDetailModel])
async def get_reserved_item_by_inventory(
    inventory_id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
    user: User = Depends(AuthService.get_current_user),
):
    if Role.ADMIN not in user.roles and Role.USER not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    username = user.username
    logger.debug("getReservedItemByInventory: username={}", username)
    logger.debug("getReservedItemByInventory: inventory_id={}", inventory_id)
    reserved_item = await service.find_reserved_item_by_inventory(str(inventory_id))
    if not reserved_item:
        raise NotFoundException(inventory_id=inventory_id)
    reserved_item_model = [ReservationDetailModel.model_validate(item) for item in reserved_item]
    logger.success("Found reserved item: {}", reserved_item_model)
    return reserved_item_model

@router.get("/reserve/{inventory_id}", response_model=ReservationModel)
async def get_reserved_item_by_inventory(
    inventory_id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    logger.debug("getReservedItemByInventory: username={}, inventory_id={}", username, inventory_id)
    reserved_item = await service.find_reserved_item_by_username(username, str(inventory_id))
    if not reserved_item:
        raise NotFoundException(username=username, id=inventory_id)
    reserved_item_model = ReservationModel.model_validate(reserved_item)
    logger.success("Found reserved item: {}", reserved_item_model)
    return reserved_item_model

@router.get("/full/{id}", response_model=InventoryFullModel)
async def get_by_id_full(
    id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
):
    logger.debug("getByIdFull: {}", id)
    inventory = await service.find_by_id(str(id), True, False)
    if not inventory:
        raise NotFoundException(id=id)
    return to_full_model(inventory)

@router.get("/{id}", response_model=InventoryModel)
async def get_by_id(
    id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    token: str = Depends(AuthService.get_bearer_token),
):
    logger.debug("getById: {}", id)
    inventory = await service.find_by_id(str(id), False, False)
    if not inventory:
        raise NotFoundException(id=id)
    inventory_model = to_model(inventory)
    return inventory_model

def to_model(inventory: InventoryBase) -> InventoryModel:
    inventory_model = InventoryModel.model_validate(inventory)
    logger.debug("Found inventory: {}", inventory_model)
    return inventory_model

def to_full_model(inventory: InventoryBase) -> Optional[InventoryFullModel]:
    if not inventory.reserved_items:
        return None
    logger.debug("to_reserve: inventory={}", inventory)
    reserved_items_models = [ReservationDetailModel.model_validate(item) for item in inventory.reserved_items]
    inventory_full_model = InventoryFullModel.model_validate(inventory)
    inventory_full_model.reserved_items = reserved_items_models
    logger.debug("Found inventory (full): {}", inventory_full_model)
    return inventory_full_model

# @router.get("/", response_model=List[InventoryRead])
# async def read_inventories(
#     skip: int = 0, limit: int = 10, db: AsyncSession = Depends(get_session)
# ):
#     inventories = await get_inventories(db)
#     return inventories
