from uuid import UUID
from fastapi import APIRouter, Depends, HTTPException, Path

from app.exception.not_found import NotFoundException
from ..core import Logger
from ..schemas import ProductSchema, SearchCriteria, ProductModel
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
    try:
        product = await read_product_service.find_by_id(product_id)
        logger.debug("Product found: {}", product)
        return product
    except NotFoundException as e:
        logger.error("Product not found: {}", str(e))
        raise HTTPException(status_code=404, detail="Product not found")
    except Exception as e:
        logger.error("Unexpected error: {}", str(e))
        raise HTTPException(status_code=500, detail="Internal server error")


@router.get("/", response_model=list[ProductSchema])
async def find_products(
    search_criteria: SearchCriteria = Depends(),
    read_product_service: ProductReadService = Depends(ProductReadService),
):
    products = await read_product_service.find_products(search_criteria)
    return products


def to_model(product: ProductSchema) -> ProductModel:
    product_model = ProductModel.model_validate(product)
    logger.debug("Found product: {}", product_model)
    return product_model
