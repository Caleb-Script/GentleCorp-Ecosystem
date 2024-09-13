from fastapi import HTTPException


class CustomHTTPException(HTTPException):
    pass


def product_not_found_exception(product_id: int):
    return HTTPException(
        status_code=404, detail=f"Product with ID {product_id} not found"
    )
