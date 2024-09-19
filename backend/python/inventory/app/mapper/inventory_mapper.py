from app.models.inventory import Inventory
from app.schemas import InventoryBase, InventoryModel, InventoryCreateModel

class InventoryMapper:
    def to_base(inventory: Inventory) -> InventoryBase:
        return InventoryBase(
            id=inventory.id,
            sku_code=inventory.sku_code,
            quantity=inventory.quantity,
            unit_price=inventory.unit_price,
            status=inventory.status,
            product_id=inventory.product_id
        )

    def to_model(inventory: Inventory) -> InventoryModel:
        return InventoryModel(
            id=inventory.id,
            sku_code=inventory.sku_code,
            quantity=inventory.quantity,
            unit_price=inventory.unit_price,
            status=inventory.status,
            product_id=inventory.product_id,
            created_at=inventory.created_at,
            updated_at=inventory.updated_at
        )

    def to_inventory(self, inventory_create: InventoryCreateModel) -> Inventory:
        return Inventory(
            version=0,
            sku_code="",
            quantity=inventory_create.quantity,
            unit_price=inventory_create.unit_price,
            status=inventory_create.status,
            product_id=inventory_create.product_id
        )