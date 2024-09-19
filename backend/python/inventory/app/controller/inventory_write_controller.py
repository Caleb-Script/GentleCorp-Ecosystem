from fastapi import APIRouter, Body, Depends, HTTPException, Response, status

from ..core import custom_logger
from ..schemas import InventoryCreateModel, InventoryUpdate, ReservationModel
from ..service import InventoryWriteService
from ..security import AuthService, User, Role

router = APIRouter()
logger = custom_logger(__name__)


@router.post("/", response_model=str)
async def create_inventory(
    inventory: InventoryCreateModel,
    service: InventoryWriteService = Depends(InventoryWriteService),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    logger.debug("createInventory: inventory={}, role={}", inventory, user)

    # Correct role-checking logic
    if Role.ADMIN not in user.roles and Role.USER not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")   
    created_inventory_id = await service.create(inventory)
    response.headers["Location"] = f"/inventory/{created_inventory_id}"
    response.status_code = status.HTTP_201_CREATED
    logger.success("createInventory; new inventory id={}", created_inventory_id)
    return response


@router.put("/{id}")
async def update_inventory(
    id: str,
    inventory_data: InventoryUpdate,
    service: InventoryWriteService = Depends(),
    user: User = Depends(AuthService.get_current_user),
    response: Response = Response(),
):
    if Role.ADMIN not in user.roles and Role.USER not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    inventory = await service.update(id, inventory_data)
    response.status_code = status.HTTP_204_NO_CONTENT
    logger.success("createInventory; new inventory id={}", inventory.id)
    return response


@router.delete("/{id}", response_model=str)
async def delete_inventory(
    id: str,
    service: InventoryWriteService = Depends(),
    user: User = Depends(AuthService.get_current_user),
    response: Response = Response(),
):
    if Role.ADMIN not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    inventory = await service.delete(id)
    response.status_code = status.HTTP_204_NO_CONTENT
    logger.success("createInventory; new inventory id={}", inventory.id)
    return response

# TODO ID in location header
@router.post("/{id}/item")
async def reserve_item(
    id: str,
    item: ReservationModel,
    service: InventoryWriteService = Depends(),
    user: User = Depends(AuthService.get_current_user),
):
    if Role.ADMIN not in user.roles and Role.USER not in user.roles and Role.SUPREME not in user.roles and Role.ELITE not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    username = user.username
    logger.debug("reserve_item: id={}, item={}, username={}", id, item, username)
    reserved_item_id = await service.reseveItem(id, item, username)
    return reserved_item_id
