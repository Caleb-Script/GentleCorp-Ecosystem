from sqlalchemy import Column, Integer, ForeignKey
from sqlalchemy.dialects.mysql import CHAR
from sqlalchemy.orm import relationship
import uuid
from ..db import Base


class ReservedProduct(Base):
    __tablename__ = "reserved_products"

    id = Column(
        CHAR(36), primary_key=True, default=lambda: str(uuid.uuid4())
    )  # UUID als CHAR
    quantity = Column(Integer, nullable=False)
    inventory_id = Column(CHAR(36), ForeignKey("inventory.id", ondelete="CASCADE"))
    inventory = relationship("Inventory", back_populates="reserved_products")
