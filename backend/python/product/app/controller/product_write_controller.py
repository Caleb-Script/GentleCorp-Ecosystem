from uuid import UUID

from fastapi import APIRouter, Depends, Header, HTTPException, Response, status

from ..core import Logger
from ..exception import (
    InvalidException,
    UnauthorizedError,
    VersionConflictException,
    VersionMissingException,
)
from ..schemas import ProductCreateSchema, ProductUpdateModel
from ..security import AuthService, Role, User
from ..service import ProductWriteService

router = APIRouter()
logger = Logger("ProductWriteController")


@router.post("/")
async def create_product(
    product: ProductCreateSchema,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
    response: Response = Response(),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles:
        raise UnauthorizedError(username=username, roles=roles)
    logger.info("Produkt wird erstellt: {}", product)
    created_product = await write_product_service.create_product(product)
    logger.success("Produkt erstellt: {}", created_product)
    response.headers["Location"] = f"/product/{created_product}"
    response.status_code = status.HTTP_201_CREATED
    logger.success("createProduct; new product id={}", created_product)
    return response


# TODO logger clean up


@router.put("/{product_id}")
async def update_product(
    product_id: UUID,
    product: ProductUpdateModel,
    user: User = Depends(AuthService.get_current_user),
    write_product_service: ProductWriteService = Depends(ProductWriteService),
    response: Response = Response(),
    if_match: str = Header(None),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedError(username=username, roles=roles)
    version = get_version(if_match)
    logger.info("Produkt wird aktualisiert: {}", product)
    updated_product = await write_product_service.update(product_id, product, version)
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
    if_match: str = Header(None),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles:
        raise UnauthorizedError(username=username, roles=roles)
    version = get_version(if_match)
    if not await write_product_service.delete_product(product_id, version):
        raise HTTPException(status_code=404, detail="Produkt nicht gefunden")
    response.status_code = status.HTTP_204_NO_CONTENT
    logger.success("deleteProduct; product id={}", product_id)
    return Response(status_code=status.HTTP_204_NO_CONTENT)


def get_version(if_match: str | None) -> int:
    if if_match is None:
        raise VersionMissingException()
    try:
        version = int(if_match.strip('"'))
        logger.debug("get_version: version={}", version)
        return version
    except ValueError:
        raise InvalidException(if_match)
