from fastapi import HTTPException, status
from databases import Database
from sqlalchemy import text
from pathlib import Path
import aiofiles  # Importieren von aiofiles für asynchrone Dateioperationen
from ..core import custom_logger, settings

logger = custom_logger(__name__)


class AdminService:

    async def execute_statements(self, database: Database, script_path: str) -> None:
        """Execute SQL statements from a file asynchronously."""
        if not Path(script_path).is_file():
            logger.error(f"Script file not found: {script_path}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Script file not found",
            )

        async with aiofiles.open(script_path, "r") as file:  # Verwenden von aiofiles
            statements = await file.read()  # Asynchrones Lesen der Datei
            statements = statements.split(";")
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
                                detail=f"Error executing statement: {str(e)}",
                            )

    async def populate_inventory_test_data(self):
        """Populate the database with test data."""
        database = Database(settings.ADMIN_DATABASE_URL)

        try:
            await database.connect()

            # Drop existing tables
            drop_script = (
                Path(__file__).resolve().parent.parent.parent
                / ".extras"
                / "sql"
                / "drop.sql"
            )
            logger.info(f"Executing drop script: {drop_script}")
            await self.execute_statements(database, drop_script)
            logger.success(f"Executed drop script: {drop_script}")

            # Create new tables
            create_script = (
                Path(__file__).resolve().parent.parent.parent
                / ".extras"
                / "sql"
                / "create.sql"
            )
            logger.info(f"Executing create script: {create_script}")
            await self.execute_statements(database, create_script)
            logger.success(f"Executed create script: {create_script}")

            # Load data into tables
            tables = ["inventory", "reserved_item"]
            csvDir = "/var/lib/mysql-files/inventory"
            # LOAD DATA LOCAL INFILE '{Path(__file__).resolve().parent.parent.parent / ".extras" / "csv" / f"{table}.csv"}'
            for table in tables:
                load_data_script = f"""
                LOAD DATA INFILE '{csvDir}/{table}.csv'
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
