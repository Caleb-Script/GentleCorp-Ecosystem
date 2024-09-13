from fastapi import APIRouter, Body, Depends, HTTPException, Response, status

from ..core import custom_logger
from ..schemas import InventoryCreate, InventoryUpdate
from ..service import InventoryWriteService
from..security import AuthService, User, Role

router = APIRouter()
logger = custom_logger(__name__)


@router.post("/", response_model=str)
async def create_inventory(
    inventory: InventoryCreate,
    service: InventoryWriteService = Depends(InventoryWriteService),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    logger.debug("createInventory: inventory={}, role={}", inventory, user)
    try:
        # Correct role-checking logic
        if Role.ADMIN not in user.roles and Role.USER not in user.roles:
            raise HTTPException(status_code=403, detail="Not enough permissions")

        created_inventory = await service.create(inventory)
        response.headers["Location"] = f"/inventory/{created_inventory.id}"
        response.status_code = status.HTTP_201_CREATED
        logger.success("createInventory; new inventory id={}", created_inventory.id)
        return response
    except Exception as e:
        logger.error(f"Error creating inventory: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
        )


@router.put("/{id}")
async def update_inventory(
    id: str,
    inventory_data: InventoryUpdate,
    service: InventoryWriteService = Depends(),
):
    inventory = await service.update(id, inventory_data)
    if not inventory:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Inventory not found"
        )
    return inventory.id


@router.delete("/{id}", response_model=str)
async def delete_inventory(id: str, service: InventoryWriteService = Depends()):
    inventory = await service.delete(id)
    if not inventory:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Inventory not found"
        )
    return inventory.id
