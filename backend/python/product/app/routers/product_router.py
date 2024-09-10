from fastapi import APIRouter, Depends, HTTPException, Query
from typing import List, Optional
from app import crud, schemas
from ..models.product import ProductCategoryType
from app.security.security import get_current_user, requires_role, User

router = APIRouter()


@router.post("/", status_code=201)
def create_product(
    product: schemas.product.ProductSchema, user: User = Depends(get_current_user)
):
    print(f"create_product: user={user}")
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    product_id = crud.create_product(product)
    return {"id": product_id}


@router.get("/{product_id}", response_model=schemas.product.ProductSchema)
def get_product_by_id(product_id: str):
    product = crud.get_product(product_id)
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
        if any([name, brand, category, min_price, max_price]):
            filtered_products = crud.filter_products(
                name=name,
                brand=brand,
                category=category,
                min_price=min_price,
                max_price=max_price,
            )
            return filtered_products
        else:
            return crud.get_all_products()
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.delete("/{product_id}")
def delete_product(product_id: str, user: User = Depends(get_current_user)):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Not enough permissions")
    crud.delete_product(product_id)
    return {"detail": "Product deleted"}
