from fastapi import APIRouter, HTTPException, status
from databases import Database
from sqlalchemy import text
import os
from pathlib import Path

from ..core import custom_logger, settings

router = APIRouter()
logger = custom_logger(__name__)


async def execute_statements(database: Database, script_path: str) -> None:
    """Execute SQL statements from a file asynchronously."""
    if not os.path.isfile(script_path):
        logger.error(f"Script file not found: {script_path}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Script file not found",
        )

    with open(script_path, "r") as file:
        statements = file.read().split(";")
        async with database.transaction():
            for statement in statements:
                statement = statement.strip()
                if statement:
                    try:
                        await database.execute(text(statement))
                    except Exception as e:
                        logger.error(f"Error executing statement: {statement}\n{e}")
                        raise HTTPException(
                            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                            detail=str(e),
                        )


@router.post("/db_populate", tags=["Admin"])
async def db_populate():
    """Populate the database with test data."""
    database = Database(settings.DATABASE_URL, local_infile=True)

    try:
        await database.connect()

        # Drop existing tables
        drop_script = (
            Path(__file__).resolve().parent.parent.parent / ".extras" / "sql" / "drop.sql"
        )
        logger.info(f"Executing drop script: {drop_script}")
        await execute_statements(database, drop_script)
        logger.success(f"Executed drop script: {drop_script}")

        # Create new tables
        create_script = (
            Path(__file__).resolve().parent.parent.parent / ".extras" / "sql" / "create.sql"
        )
        logger.info(f"Executing create script: {create_script}")
        await execute_statements(database, create_script)
        logger.success(f"Executed create script: {create_script}")

        # Load data into tables
        tables = ["inventory", "reserved_item"]
        for table in tables:
            load_data_script = f"""
            LOAD DATA LOCAL INFILE '{Path(__file__).resolve().parent.parent.parent / ".extras" / "csv" / f"{table}.csv"}'
            INTO TABLE {table}
            FIELDS TERMINATED BY ';'
            ENCLOSED BY '"'
            LINES TERMINATED BY '\\n'
            IGNORE 1 ROWS;
            """
            logger.info(f"Executing load data script for table {table}")
            try:
                await database.execute(text(load_data_script))
            except Exception as e:
                logger.error(f"Error loading data into table {table}\n{e}")
                raise HTTPException(
                    status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
                )
            logger.success(f"Executed load data script for table {table}")
        return {"db_populate": "success"}
    except Exception as e:
        logger.error(f"Database population failed: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
        )
    finally:
        await database.disconnect()
