from uuid import UUID
from fastapi import APIRouter, Depends, HTTPException, Response, status

from ..exception import DuplicateException
from ..core import Logger
from ..schemas import ProductModel, ProductUpdateModel
from ..security import AuthService, User
from ..service import ProductWriteService

router = APIRouter()
logger = Logger("ProductWriteController")


@router.post("/")
async def create_product(
    product: ProductModel,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
    response: Response = Response(),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    logger.info("Produkt wird erstellt: {}", product)
    created_product = await write_product_service.create_product(product)
    logger.success("Produkt erstellt: {}", created_product)
    response.headers["Location"] = f"/product/{created_product}"
    response.status_code = status.HTTP_201_CREATED
    logger.success("createProduct; new product id={}", created_product)
    return response
    


@router.put("/{product_id}")
async def update_product(
    product_id: UUID,
    product: ProductUpdateModel,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
    response: Response = Response(),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    logger.info("Produkt wird aktualisiert: {}", product)
    updated_product = await write_product_service.update_product(product_id, product)
    if not updated_product:
        raise HTTPException(status_code=404, detail="Produkt nicht gefunden")
    response.status_code = status.HTTP_204_NO_CONTENT
    logger.success("updateProduct; product id={}", product_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)

@router.delete("/{product_id}")
async def delete_product(
    product_id: UUID,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
    response: Response = Response(),
):
    if "ADMIN" not in user.roles:
        raise HTTPException(status_code=403, detail="Nicht genügend Berechtigungen")
    if not await write_product_service.delete_product(product_id):
        raise HTTPException(status_code=404, detail="Produkt nicht gefunden")
    response.status_code = status.HTTP_204_NO_CONTENT
    logger.success("deleteProduct; product id={}", product_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)
