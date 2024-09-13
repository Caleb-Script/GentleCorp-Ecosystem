# app/controller/__init__.py
from .inventory_read_controller import router as inventory_read_controller
from .inventory_write_controller import router as inventory_write_controller
from .admin_controller import router as admin_controller
from .auth_controller import router as auth_controller
