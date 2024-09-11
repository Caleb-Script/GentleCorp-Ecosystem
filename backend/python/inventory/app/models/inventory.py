from sqlalchemy import Column, String, Integer, Enum, ForeignKey, DECIMAL
from sqlalchemy.dialects.mysql import CHAR
from sqlalchemy.orm import relationship
import uuid
from app.db.mysql import Base
import enum


class InventoryStatusType(enum.Enum):
    DISCONTINUED = "D"
    AVAILABLE = "A"
    RESERVED = "R"
    OUT_OF_STOCK = "O"


class Inventory(Base):
    __tablename__ = "inventory"

    id = Column(
        CHAR(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )  # UUID als CHAR
    sku_code = Column(String(50), nullable=False)  # Länge für VARCHAR angeben
    quantity = Column(Integer, nullable=False)
    unit_price = Column(
        DECIMAL(10, 2), nullable=False
    )  # Präzision und Skala für DECIMAL angeben
    status = Column(Enum(InventoryStatusType), nullable=False)
    product_id = Column(CHAR(36), nullable=False)  # UUID als CHAR

    reserved_products = relationship("ReservedProduct", back_populates="inventory")


class ReservedProduct(Base):
    __tablename__ = "reserved_products"

    id = Column(
        CHAR(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )  # UUID als CHAR
    sku_code = Column(String(50), nullable=False)  # Länge für VARCHAR angeben
    quantity = Column(Integer, nullable=False)
    inventory_id = Column(CHAR(36), ForeignKey("inventory.id"))
    inventory = relationship("Inventory", back_populates="reserved_products")
