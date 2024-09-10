from fastapi import FastAPI
from .db import client
from . import crud, schemas

app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.post("/items/")
def create_item(item: schemas.Item):
    return crud.create_item(item)

@app.get("/items/{item_id}")
def read_item(item_id: str):
    return crud.get_item(item_id)
