from typing import List, Optional
from fastapi import (
    APIRouter,
    Depends,
    HTTPException,
    status,
    Header,
    Path,
    Response,
)
from uuid import UUID

from ..security import User, Role
from ..core import custom_logger
from ..schemas import InventoryBase, InventoryModel, SearchParams, InventoryFullModel, ReservationFullModel, ReservationModel, ReservationDetailModel
from ..service import AuthService, InventoryReadService
from ..exceptions import (
    NotFoundException,
    UnauthorizedException,
    VersionMissingException,
    InvalidArgumentException,
)

router = APIRouter()
logger = custom_logger("inventory_read_controller")


@router.get("/", response_model=List[InventoryBase])
async def get_all(
    search_params: SearchParams = Depends(),
    service: InventoryReadService = Depends(InventoryReadService),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedException(username=username, roles=roles)
    logger.debug("get_all: search_params=[{}] | user={}", search_params, username)
    inventories = await service.find(search_params)
    if not inventories:
        raise NotFoundException()
    logger.debug("get_all: inventories={} | user={}", inventories, username)
    response.status_code = status.HTTP_200_OK
    return inventories


@router.get("/reserve", response_model=List[ReservationFullModel])
async def get_all_resevered_items_by_username(
    service: InventoryReadService = Depends(InventoryReadService),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    roles = user.roles
    if (
        Role.ADMIN not in roles
        and Role.USER not in roles
        and Role.SUPREME not in roles
        and Role.ELITE not in roles
    ):
        raise UnauthorizedException(username=username, roles=roles)
    username = user.username
    logger.debug("get_all_resevered_items_by_username: user={}", username)
    reserved_items = await service.find_by_username(username)
    if not reserved_items:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="No reserved items found")
    reserved_items_models = [ReservationFullModel.model_validate(item) for item in reserved_items]
    response.status_code = status.HTTP_200_OK
    logger.debug(
        "get_all_resevered_items_by_username: reserved_items_models={} | user={}",
        reserved_items_models, username
    )
    return reserved_items_models


@router.get("/reserved/{inventory_id}", response_model=List[ReservationDetailModel])
async def get_reserved_items_by_inventory(
    inventory_id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    user: User = Depends(AuthService.get_current_user),
    response: Response = Response(),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedException(username=username, roles=roles)
    logger.debug("get_reserved_items_by_inventory: inventory_id={} | username={}", inventory_id, username)
    reserved_item = await service.find_reserved_item_by_inventory(str(inventory_id))
    if not reserved_item:
        raise NotFoundException(inventory_id=inventory_id)
    reserved_item_model = [ReservationDetailModel.model_validate(item) for item in reserved_item]
    logger.debug("et_reserved_items_by_inventory: {} | user={}", reserved_item_model, username)
    response.status_code = status.HTTP_200_OK
    return reserved_item_model


@router.get("/reserve/{inventory_id}", response_model=ReservationModel)
async def get_reserved_item_by_inventory_and_user(
    inventory_id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    if_none_match: str = Header(None),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    roles = user.roles
    if (
        Role.ADMIN not in roles
        and Role.USER not in roles
        and Role.SUPREME not in roles
        and Role.ELITE not in roles
    ):
        raise UnauthorizedException(username=username, roles=roles)
    logger.debug(
        "get_reserved_item_by_inventory_and_user: inventory_id={} | username={}",
        inventory_id,
        username,
    )
    version = get_version(if_none_match)
    reserved_item = await service.find_reserved_item_by_username(username, str(inventory_id))
    if not reserved_item:
        raise NotFoundException(username=username, id=inventory_id)
    if reserved_item.version == version:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        return response
    reserved_item_model = ReservationModel.model_validate(reserved_item)
    logger.success(
        "get_reserved_item_by_inventory_and_user: reserved_item_model={} | username={}",
        reserved_item_model,
        username
    )
    response.status_code = status.HTTP_200_OK
    return reserved_item_model


@router.get("/full/{id}", response_model=InventoryFullModel)
async def get_by_id_full(
    id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    if_none_match: str = Header(None),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedException(username=username, roles=roles)
    logger.debug("get_by_id_full: id={} | user={}", id, username)
    version = get_version(if_none_match)
    inventory = await service.find_by_id(str(id), True, False)
    if not inventory:
        raise NotFoundException(id=id)
    if inventory.version == version:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        return response
    logger.debug("get_by_id_full: inventory={} | user={}", inventory, username)
    response.status_code = status.HTTP_200_OK
    return to_full_model(inventory)


@router.get("/{id}", response_model=InventoryModel)
async def get_by_id(
    id: UUID,
    service: InventoryReadService = Depends(InventoryReadService),
    if_none_match: str = Header(None),
    response: Response = Response(),
    user: User = Depends(AuthService.get_current_user),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedException(username=username, roles=roles)
    logger.debug("get_by_id(: id={} | user={}", id, username)
    version = get_version(if_none_match)
    inventory = await service.find_by_id(str(id), False, False)
    if not inventory:
        raise NotFoundException(id=id)
    if inventory.version == version:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        return response
    logger.debug("get_by_id: inventory={} | user={}", inventory, username)
    response.status_code = status.HTTP_200_OK
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


def get_version(if_none_match: str | None) -> int:
    if if_none_match is None:
        raise VersionMissingException()
    try:
        version = int(if_none_match.strip('"'))
        logger.debug("get_version: version={}", version)
        return version
    except ValueError:
        raise InvalidArgumentException(if_none_match)
