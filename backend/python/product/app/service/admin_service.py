import csv
import json
import os
from pathlib import Path

from fastapi import HTTPException, status
from motor.motor_asyncio import AsyncIOMotorClient

from ..core import Logger, settings

logger = Logger(__name__)


class AdminService:
    def __init__(self):
        self.client = AsyncIOMotorClient(settings.DATABASE_URL)
        self.database = self.client[settings.DATABASE_NAME]

    async def execute_json_statements(self, script_path: str) -> None:
        logger.info(f"Executing script: {script_path}")
        """Execute MongoDB statements from a JSON file asynchronously."""
        if not os.path.isfile(script_path):
            logger.error(f"Script file not found: {script_path}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Script file not found",
            )

        with open(script_path, "r") as file:
            statements = json.load(file)
            for statement in statements:
                collection = self.database[statement["collection"]]
                operation = statement["operation"]
                data = statement["data"]
                try:
                    if operation == "insert_many":
                        await collection.insert_many(data)
                    elif operation == "delete_many":
                        await collection.delete_many(data)
                    elif operation == "create_index":
                        await collection.create_index(data)
                except Exception as e:
                    logger.error(f"Error executing statement: {statement}\n{e}")
                    raise HTTPException(
                        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                        detail=str(e),
                    )

    async def populate_database(self):
        try:
            # Drop existing collections
            drop_script = (
                Path(__file__).resolve().parent.parent.parent
                / ".extras"
                / "json"
                / "drop.json"
            )
            logger.info(f"Executing drop script: {drop_script}")
            await self.execute_json_statements(drop_script)
            logger.success(f"Executed drop script: {drop_script}")

            # Create new collections and indexes
            create_script = (
                Path(__file__).resolve().parent.parent.parent
                / ".extras"
                / "json"
                / "create.json"
            )
            logger.info(f"Executing create script: {create_script}")
            await self.execute_json_statements(create_script)
            logger.success(f"Executed create script: {create_script}")

            # Load data into collections
            collections = ["product"]
            for collection_name in collections:
                csv_path = (
                    Path(__file__).resolve().parent.parent.parent
                    / ".extras"
                    / "csv"
                    / f"{collection_name}.csv"
                )
                logger.info(f"Loading data into collection {collection_name}")
                try:
                    with open(csv_path, "r") as csvfile:
                        csvreader = csv.DictReader(csvfile, delimiter=";")
                        data = [row for row in csvreader]
                        await self.database[collection_name].insert_many(data)
                except Exception as e:
                    logger.error(
                        f"Error loading data into collection {collection_name}\n{e}"
                    )
                    raise HTTPException(
                        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e)
                    )
                logger.success(f"Loaded data into collection {collection_name}")
        finally:
            self.client.close()
