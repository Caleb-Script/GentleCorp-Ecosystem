from .db import db
from .models import Item

def create_item(item: Item):
    collection = db["items"]
    result = collection.insert_one(item.dict())
    return {"id": str(result.inserted_id)}

def get_item(item_id: str):
    collection = db["items"]
    item = collection.find_one({"_id": item_id})
    return item
