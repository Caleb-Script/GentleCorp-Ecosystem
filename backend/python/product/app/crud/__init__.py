# app/crud/__init__.py
from .read_product import (
  get_product,
  get_all_products,
  filter_products
)

from .write_product import (
    create_product,
    delete_product,
)
