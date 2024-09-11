import logging
from typing import Dict, List, Optional

from app import crud, db, schemas
from app.security.security import User, get_current_user
from fastapi import APIRouter, Depends, HTTPException, Query

from ..exception.exception import DuplicateKeyError
from ..models.product import ProductCategoryType

router = APIRouter()
logger = logging.getLogger("uvicorn")

@router.post("/", status_code=201)
def create_product(
    product: schemas.product.ProductSchema, user: User = Depends(get_current_user)
):
    try:
        logger.info(f"create_product: user={user}")
        if "ADMIN" not in user.roles:
            raise HTTPException(status_code=403, detail="Not enough permissions")
        product_id = crud.create_product(product)
        return {"id": product_id}
    except DuplicateKeyError:
        logger.error("Duplicate key error while creating product")
        raise DuplicateKeyError(name=product.name)

    except Exception as e:
        logger.error(f"Error creating product: {e}")
        raise HTTPException(status_code=500, detail="Internal server error")


@router.get("/{product_id}", response_model=schemas.product.ProductSchema)
def get_product_by_id(product_id: str):
    product = crud.find_by_id(product_id)
    if product:
        return product
    raise HTTPException(status_code=404, detail="Product not found")


@router.get("/", response_model=List[schemas.product.ProductSchema])
def get_products(
    name: Optional[str] = Query(None),
    brand: Optional[str] = Query(None),
    category: Optional[ProductCategoryType] = Query(None),
    min_price: Optional[float] = Query(None),
    max_price: Optional[float] = Query(None),
):
    try:
        # Call the combined function for retrieving products
        products = crud.find_products(
            name=name,
            brand=brand,
            category=category,
            min_price=min_price,
            max_price=max_price,
        )
        return products
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.delete("/{product_id}")
def delete_product(product_id: str, user: User = Depends(get_current_user)):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    crud.delete_product(product_id)
    return {"detail": "Product deleted"}


@router.post("/db_populate")
def populate_db():
    print(f"Populating")
    db.insert_example_data()
    return {"detail": "Database populated with example products"}
