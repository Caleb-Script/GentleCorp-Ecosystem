from uuid import UUID

from fastapi import APIRouter, Depends, Header, Path, Response, status

from ..exception import UnauthorizedError, VersionMissingException, InvalidException
from ..core import Logger
from ..schemas import ProductModel, ProductSchema, SearchCriteria
from ..security import AuthService, User, Role
from ..service import ProductReadService
from ..models import Product

router = APIRouter()
logger = Logger(__name__)


@router.get("/{product_id}", response_model=ProductModel)
async def get_product_by_id(
    product_id: UUID = Path(..., description="The ID of the product to retrieve"),
    user: User = Depends(AuthService.get_current_user),
    read_product_service: ProductReadService = Depends(ProductReadService),
    if_none_match: str = Header(None),
    response: Response = Response()
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedError(username=username, roles=roles)
    version = get_version(if_none_match)
    logger.debug("get_product_by_id: id={}, username={}", product_id, username)
    product = await read_product_service.find_by_id(product_id)
    if product.version == version:
        response.status_code = status.HTTP_304_NOT_MODIFIED
        return response

    response.status_code = status.HTTP_200_OK
    logger.debug("Product found: {}", product)
    return product


@router.get("/", response_model=list[ProductSchema])
async def find_products(
    search_criteria: SearchCriteria = Depends(),
    user: User = Depends(AuthService.get_current_user),
    read_product_service: ProductReadService = Depends(ProductReadService),
):
    username = user.username
    roles = user.roles
    if Role.ADMIN not in roles and Role.USER not in roles:
        raise UnauthorizedError(username=username, roles=roles)
    products = await read_product_service.find_all(search_criteria)
    products_schema = [ProductSchema.model_dump(product) for product in products]
    return products_schema


def get_version(if_none_match: str | None) -> int:
    if if_none_match is None:
        raise VersionMissingException()
    try:
        version = int(if_none_match.strip('"'))
        logger.debug("get_version: version={}", version)
        return version
    except ValueError:
        raise InvalidException(if_none_match)
