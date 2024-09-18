from fastapi import HTTPException


class VersionConflictException(HTTPException):
    def __init__(self, product_id: str, current_version: int, requested_version: int):

        super().__init__(
            status_code=409,
            detail=f"Version conflict for product {product_id}. Current version is {current_version}, but version {requested_version} was requested.",
        )
        self.product_id = product_id
        self.current_version = current_version
        self.requested_version = requested_version
