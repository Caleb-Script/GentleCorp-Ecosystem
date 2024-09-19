from sqlalchemy import Column, ForeignKey, String, Integer, Enum, DECIMAL
from sqlalchemy.dialects.mysql import CHAR
from sqlalchemy.orm import relationship
import uuid
from ..db import Base
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
    version = Column(Integer, nullable=False)
    sku_code = Column(String(50), nullable=False)
    quantity = Column(Integer, nullable=False)
    unit_price = Column(
        DECIMAL(10, 2), nullable=False
    )
    status = Column(Enum(InventoryStatusType), nullable=False)
    product_id = Column(CHAR(36), unique=True, nullable=False)

    reserved_items = relationship(
        "ReservedItem", back_populates="inventory", cascade="all, delete-orphan"
    )


class ReservedItem(Base):
    __tablename__ = "reserved_item"

    id = Column(
        CHAR(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )
    version = Column(Integer, nullable=False)
    quantity = Column(Integer, nullable=False)
    username = Column(String(255), nullable=False)
    inventory_id = Column(CHAR(36), ForeignKey("inventory.id", ondelete="CASCADE"))
    inventory = relationship("Inventory", back_populates="reserved_items")
