from typing import Optional
from uuid import UUID

from app.models.inventory import Inventory
from fastapi import Depends, HTTPException

from ..clients import ProductClient
from ..service import InventoryReadService
from ..repository import InventoryRepository
from ..schemas import InventoryModel, InventoryBase, InventoryUpdate, ReservationModel, InventoryCreateModel
from ..mapper import InventoryMapper
from ..core import custom_logger
from ..exceptions import UnzureichenderBestandFehler
from app.models import ReservedItem
from app.exceptions import UnzureichenderBestandFehler, NotFoundException

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

    # TODO DuplicateException implementieren
    async def create(self, inventory_create: InventoryCreateModel) -> UUID:
        logger.debug("create: inventory_create={}", inventory_create)
        product_id = inventory_create.product_id
        try:
            product = await self.product_repository.get_by_id(inventory_create.product_id, "-1")
            logger.debug("create: product={}", product)
            sku_code = generate_sku_code(product.brand, product.name)
        except NotFoundException as not_found:
            logger.error("create: Kein Produkt mit der ID: {} gefunden", product_id)
            raise NotFoundException(product_id=product_id)
        except HTTPException as e:
            logger.warning(e.detail)
            product = None

        inventory = self.inventoryMapper.to_inventory(inventory_create)
        inventory.sku_code = sku_code
        created_inventory = await self.repository.create_inventory(inventory)
        logger.debug("create: created_inventory={}", created_inventory)
        return created_inventory.id

    async def update(self, id: str, inventory_data: InventoryUpdate) -> InventoryBase:

        inventory = await self.inventoryReadService.find_by_id(id, True, False)

        update_data = {
            field: value
            for field, value in inventory_data.model_dump(exclude_unset=True).items()
        }

        for field, value in update_data.items():
            setattr(inventory, field, value)

        await self.repository.update_inventory(id, inventory)
        return InventoryBase.model_validate(inventory)

    async def delete(self, id: str) -> Optional[InventoryModel]:
        return await self.repository.delete_inventory(id)

    async def reseveItem(
        self, id: str, item: ReservationModel, username: str
    ) -> UUID:
        inventory = await self.inventoryReadService.find_by_id(id, True, True)

        if not inventory:
            raise NotFoundException(id=id)

        übrigeQuantity = await self.inventoryReadService.calculateQantity(inventory)
        logger.debug("Übrige Quantity: {}", übrigeQuantity)

        if übrigeQuantity < item.quantity:
            raise UnzureichenderBestandFehler(inventory.id)

        try:
            reserved_item = await self.inventoryReadService.find_reserved_item_by_username(username, id)
        except NotFoundException:
            reserved_item = None

        if reserved_item:
            # Aktualisiere die vorhandene Reservierung
            reserved_item.quantity += item.quantity
            await self.repository.update_reserved_item(reserved_item)
        else:
            # Erstelle eine neue Reservierung
            new_reserved_item = ReservedItem(
                quantity=item.quantity, username=username, inventory_id=UUID(id)
            )
            await self.repository.add_reserved_item(new_reserved_item)
            inventory.reserved_items.append(new_reserved_item)

        # # Aktualisiere die verfügbare Menge im Inventar
        # inventory.quantity -= item.quantity

        # Speichere die Änderungen
        updated_inventory = await self.repository.update_inventory(id, inventory)

        return updated_inventory.id


import re
from unidecode import unidecode
import random
import string


def generate_sku_code(brand: str, product_name: str, length: int = 11) -> str:
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
