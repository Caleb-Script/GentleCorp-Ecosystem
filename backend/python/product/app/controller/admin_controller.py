from fastapi import APIRouter, HTTPException, status, Depends

from ..db import insert_example_data
from ..core import Logger

router = APIRouter()
logger = Logger("AdminController")


@router.post("/db_populate", tags=["Admin"])
async def db_populate():
    logger.info("Starting database population")
    try:
        await insert_example_data()
        logger.info("Database population completed successfully")
        return {"db_populate": "success"}
    except Exception as e:
        logger.error(f"Database population failed: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
        )