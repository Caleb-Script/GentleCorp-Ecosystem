from fastapi import APIRouter, HTTPException, status, Depends

from ..core import Logger
from ..service import AdminService

router = APIRouter()
logger = Logger("AdminController")



@router.post("/db_populate", tags=["Admin"])
async def db_populate(admin_service: AdminService = Depends(AdminService)):
    logger.info("Populating database")
    """Populate the database with test data."""
    try:
        await admin_service.populate_database()
        return {"db_populate": "success"}
    except Exception as e:
        logger.error(f"Database population failed: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
        )
