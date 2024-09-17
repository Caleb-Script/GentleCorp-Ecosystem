from uuid import UUID
from fastapi import APIRouter, Depends, HTTPException
from ..core import Logger
from ..schemas import ProductSchema
from ..security import AuthService, User
from ..service import ProductWriteService

router = APIRouter()
logger = Logger("ProductWriteController")


@router.post("/", status_code=201)
async def create_product(
    product: ProductSchema,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    logger.info("Produkt wird erstellt: {}", product)
    created_product = await write_product_service.create_product(product)
    logger.success("Produkt erstellt: {}", created_product)
    return None


@router.put("/{product_id}", status_code=200)
async def update_product(
    product_id: UUID,
    product: ProductSchema,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    logger.info("Produkt wird aktualisiert: {}", product)
    updated_product = await write_product_service.update_product(product_id, product)
    if not updated_product:
        raise HTTPException(status_code=404, detail="Produkt nicht gefunden")
    logger.success("Produkt aktualisiert: {}", updated_product)
    return updated_product


@router.delete("/{product_id}", status_code=204)
async def delete_product(
    product_id: UUID,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    if not await write_product_service.delete_product(product_id):
        raise HTTPException(status_code=404, detail="Produkt nicht gefunden")
    return None
