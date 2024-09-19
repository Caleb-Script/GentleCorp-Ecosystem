import math
import random
import re
import string
from decimal import Decimal
from typing import Optional
from uuid import UUID

from app.models.inventory import Inventory
from fastapi import Depends, HTTPException
from unidecode import unidecode

from ..clients import ProductClient
from ..core import custom_logger
from ..exceptions import (
    DuplicateException,
    InsufficientStockException,
    NoChangesException,
    NotFoundException,
    VersionConflictException,
    VersionMissingException,
)
from ..mapper import InventoryMapper
from ..models import ReservedItem
from ..repository import InventoryRepository
from ..schemas import (
    InventoryBase,
    InventoryCreateModel,
    InventoryModel,
    InventoryUpdate,
    ReservationCreateModel,
    ReservationModel,
    SearchParams,
)
from ..service import InventoryReadService

logger = custom_logger(__name__)


class InventoryWriteService:

    def __init__(
        self,
        repository: InventoryRepository = Depends(InventoryRepository),
        inventoryReadService: InventoryReadService = Depends(InventoryReadService),
        inventoryMapper: InventoryMapper = Depends(InventoryMapper),
        product_client=Depends(ProductClient()),
    ):
        self.repository = repository
        self.inventoryReadService = inventoryReadService
        self.inventoryMapper = inventoryMapper
        self.product_repository = product_client

    async def create(self, inventory_create: InventoryCreateModel) -> UUID:
        logger.debug("create: inventory_create={}", inventory_create)
        product_id = inventory_create.product_id
        try:
            product = await self.product_repository.get_by_id(
                inventory_create.product_id, "-1"
            )
            logger.debug("create: product={}", product)
            sku_code = self.generate_sku_code(product.brand, product.name)
        except NotFoundException as not_found:
            logger.error("create: Kein Produkt mit der ID: {} gefunden", product_id)
            raise NotFoundException(product_id=product_id)
        except HTTPException as e:
            logger.warning(e.detail)
            product = None

        inventory = self.inventoryMapper.to_inventory(inventory_create)
        inventory.sku_code = sku_code
        await self.check_duplicate(product_id=product_id)
        created_inventory = await self.repository.create_inventory(inventory)
        logger.debug("create: created_inventory={}", created_inventory)
        return created_inventory.id

    async def update(
        self, id: str, inventory_data: InventoryUpdate, version: int
    ) -> None:
        inventory_db = await self.inventoryReadService.find_by_id(id, True, False)
        if inventory_db.version != version:
            logger.error("update: Konflikt bei den Versionen")
            raise VersionConflictException(id, inventory_db.version, version)

        update_data = inventory_data.model_dump(exclude_unset=True)
        logger.debug("update: update_data={}", update_data)

        if not update_data:
            logger.debug("No update data provided")
            raise NoChangesException()

        self._compare_and_update_fields(inventory_db, update_data)
        updated_inventory = await self.repository.update_inventory(id, inventory_db)
        logger.debug("update: updated_inventory={}", updated_inventory)
        return None

    def _compare_and_update_fields(self, inventory_db, update_data):
        changes_made = False
        for field, value in update_data.items():
            current_value = getattr(inventory_db, field)
            logger.debug(
                f"Comparing {field}: new={value} ({type(value)}), current={current_value} ({type(current_value)})"
            )

            if self._is_value_changed(current_value, value):
                setattr(inventory_db, field, value)
                changes_made = True
                logger.debug(f"Change detected in {field}: {current_value} -> {value}")

        if not changes_made:
            logger.debug("No changes detected")
            raise NoChangesException()

    def _is_value_changed(self, current_value, new_value):
        if isinstance(new_value, (float, Decimal)) and isinstance(
            current_value, (float, Decimal)
        ):
            return not math.isclose(
                float(new_value), float(current_value), rel_tol=1e-9, abs_tol=0.0
            )
        return current_value != new_value

    async def delete(self, id: str, version: int) -> Optional[InventoryModel]:
        inventory_db = await self.inventoryReadService.find_by_id(id, True, False)
        if inventory_db.version != version:
            logger.error("update: Konflikt bei den Versionen")
            raise VersionConflictException(id, inventory_db.version, version)
        return await self.repository.delete_inventory(id)

    async def reseveItem(
        self,
        id: str,
        item: ReservationCreateModel,
        username: str,
        version: Optional[int] = None,
    ) -> UUID:
        inventory = await self.inventoryReadService.find_by_id(id, True, True)

        if not inventory:
            raise NotFoundException(id=id)

        übrigeQuantity = await self.inventoryReadService.calculateQantity(inventory)
        logger.debug("Übrige Quantity: {}", übrigeQuantity)

        if übrigeQuantity < item.quantity:
            raise InsufficientStockException(inventory.id)

        try:
            reserved_item = (
                await self.inventoryReadService.find_reserved_item_by_username(
                    username, id
                )
            )
            if reserved_item.version != version:
                logger.error("update: Konflikt bei den Versionen")
                raise VersionConflictException(id, reserved_item.version, version)
        except NotFoundException:
            reserved_item = None

        if reserved_item:
            # Aktualisiere die vorhandene Reservierung
            reserved_item.quantity += item.quantity
            await self.repository.update_reserved_item(reserved_item)
            reserved_item_id = reserved_item.id
        else:
            # Erstelle eine neue Reservierung
            new_reserved_item = ReservedItem(
                quantity=item.quantity,
                username=username,
                inventory_id=UUID(id),
                version=0,
            )
            await self.repository.add_reserved_item(new_reserved_item)
            reserved_item_id = new_reserved_item.id
            # inventory.reserved_items.append(new_reserved_item)

        # # Aktualisiere die verfügbare Menge im Inventar
        # inventory.quantity -= item.quantity

        # Speichere die Änderungen
        # updated_inventory = await self.repository.update_inventory(id, inventory)

        return reserved_item_id

    def generate_sku_code(self, brand: str, product_name: str, length: int = 11) -> str:
        """
        Generiert einen SKU-Code basierend auf der Marke und dem Produktnamen.

        :param brand: Die Marke des Produkts
        :param product_name: Der Name des Produkts
        :param length: Die gewünschte Länge des SKU-Codes (Standard: 11)
        :return: Der generierte SKU-Code
        """
        # Bereinige und kürze die Marke
        cleaned_brand = unidecode(brand.upper())
        cleaned_brand = re.sub(r"[^A-Z]", "", cleaned_brand)
        brand_prefix = cleaned_brand[:3]

        # Bereinige und kürze den Produktnamen
        cleaned_name = unidecode(product_name.upper())
        cleaned_name = re.sub(r"[^A-Z0-9]", "", cleaned_name)
        name_part = cleaned_name[:3]

        # Kombiniere Marke und Produktname
        prefix = f"{brand_prefix}-{name_part}"

        # Fülle den Rest mit zufälligen Zahlen auf
        suffix_length = length - len(prefix) - 1  # -1 für den Bindestrich
        suffix = "".join(random.choices(string.digits, k=suffix_length))

        return f"{prefix}-{suffix}"

    async def check_duplicate(self, product_id: UUID) -> None:
        """
        Check if an inventory with the given product_id already exists.

        :param product_id: UUID of the product to check
        :param exclude_id: UUID of an inventory to exclude from the check (optional)
        :raises DuplicateException: If a duplicate inventory is found
        """
        search_criteria = SearchParams(product_id=product_id)
        inventories = await self.inventoryReadService.find(search_criteria)

        if not inventories:
            return

        inventory = inventories[0]  # We expect at most one inventory
        product = await self.product_repository.get_by_id(product_id, "-1")
        logger.error(
            "An inventory with product '{}' of brand '{}' already exists",
            product.name,
            product.brand,
        )
        raise DuplicateException(product.name, product.brand, inventory.id)
