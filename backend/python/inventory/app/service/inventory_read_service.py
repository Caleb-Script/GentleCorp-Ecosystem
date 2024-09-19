from typing import List, Optional
from fastapi import Depends, HTTPException

from ..clients import ProductClient
from ..core import custom_logger
from ..repository import InventoryRepository, ProductRepository
from ..schemas import (
    InventoryModel,
    InventoryBase,
    SearchParams,
    InventoryFullModel,
    ReservationModel,
)
from ..exceptions import NotFoundException
from ..models import Inventory

logger = custom_logger(__name__)


class InventoryReadService:

    def __init__(
        self,
        inventory_repository: InventoryRepository = Depends(InventoryRepository),
        product_client=Depends(ProductClient()),
    ):
        self.inventory_repository = inventory_repository
        self.product_repository = product_client

    async def find_by_id(self, id: str, full: bool, reservation: bool) -> InventoryBase:
        logger.debug("Getting inventory by id: {}, full={}", id, full)
        inventory = await self.inventory_repository.get_inventory_by_id(id, full)

        if inventory is None:
            raise NotFoundException(id)

        if reservation:
            inventory.name = "."
            return inventory

        try:
            product = await self.product_repository.get_by_id(inventory.product_id, "-1")
            logger.debug("find_by_id: product={}", product)
            inventory.name = product.name
            inventory.brand = product.brand

        except HTTPException as e:
            if e.status_code == 404:
                logger.warning(f"Product with id {inventory.product_id} not found")
                product = None
            else:
                raise

        logger.debug("Found inventory: {}", inventory)
        return inventory

    async def find(self, search_params: SearchParams) -> List[InventoryBase]:
        logger.debug("Searching inventory: {}", search_params)
        inventories = await self.inventory_repository.list_inventory(search_params)
        inventory_with_names = []
        for inventory in inventories:
            try:
                product = await self.product_repository.get_by_id(inventory.product_id, "-1")
                inventory.name = product.name if product else None
                inventory.brand = product.brand if product else None
            except HTTPException as e:
                if e.status_code == 404:
                    logger.warning(f"Product with id {inventory.product_id} not found")
                    product_name = None
                else:
                    raise
        # Convert SQLAlchemy models to Pydantic
        return [InventoryBase.model_validate(inventory) for inventory in inventories]

    async def calculateQantity(self, inventory: InventoryFullModel):
        logger.debug("Calculating total quantity for: {}", inventory)
        total_quantity = inventory.quantity
        for reserved_item in inventory.reserved_items:
            total_quantity -= reserved_item.quantity
        logger.debug("calculateQantity: Total quantity for: {}", total_quantity)
        return total_quantity

    async def find_by_username(self, username: str):
        logger.debug("Finding inventory by username: {}", username)
        reserved_item_list = await self.inventory_repository.get_reserved_items(username)
        logger.debug("Found reserved items: {}", reserved_item_list)
        return reserved_item_list

    async def find_reserved_item_by_username(self, username: str, inventory_id: str):
        logger.debug("Finding reserved item by username: {} and inventory_id: {}", username, inventory_id)
        reserve_item_list = await self.find_by_username(username)

        found_item = next((item for item in reserve_item_list if item.inventory_id == inventory_id), None)

        if found_item:
            return found_item
        else:
            logger.info("No reserved item found for username: {} and inventory_id: {}", username, inventory_id)
            raise NotFoundException(username, inventory_id)

    async def find_reserved_item_by_inventory(self, inventory_id: str):
        logger.debug("Finding reserved item by inventory_id: {}", inventory_id)
        reserve_item_list = await self.inventory_repository.get_reserved_items(inventory_id)
        return reserve_item_list
