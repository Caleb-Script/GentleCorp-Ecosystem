from uuid import UUID

from app.exception.not_found import NotFoundException
from fastapi import APIRouter, Depends, HTTPException, Path

from ..core import Logger
from ..schemas import ProductModel, ProductSchema, SearchCriteria
from ..security import AuthService, User
from ..service import ProductReadService

router = APIRouter()
logger = Logger(__name__)


@router.get("/{product_id}", response_model=ProductModel)
async def get_product_by_id(
    product_id: UUID = Path(..., description="The ID of the product to retrieve"),
    user: User = Depends(AuthService.get_current_user),
    read_product_service: ProductReadService = Depends(ProductReadService),
):
    logger.info("Getting product by id: {}", product_id)
    logger.debug("User: {}", user)
    logger.debug("ProductReadService: {}", read_product_service)
    product = await read_product_service.find_by_id(product_id)
    logger.debug("Product found: {}", product)
    return product


@router.get("/", response_model=list[ProductSchema])
async def find_products(
    search_criteria: SearchCriteria = Depends(),
    read_product_service: ProductReadService = Depends(ProductReadService),
):
    products = await read_product_service.find_all(search_criteria)
    products_schema = [ProductSchema.from_mongo(product) for product in products]
    return products_schema


def to_model(product: ProductSchema) -> ProductModel:
    product_model = ProductModel.model_validate(product)
    logger.debug("Found product: {}", product_model)
    return product_model
