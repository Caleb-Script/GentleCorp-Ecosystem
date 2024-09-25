from fastapi import APIRouter, Depends, HTTPException, status
from ..service import AdminService
from ..core import custom_logger

router = APIRouter()
logger = custom_logger(__name__)


@router.post("/db_populate", tags=["Admin"])
async def db_populate(service: AdminService = Depends(AdminService)):
    await service.populate_inventory_test_data()  # Aufruf der richtigen Methode
    return {"db_populate": "success"}
