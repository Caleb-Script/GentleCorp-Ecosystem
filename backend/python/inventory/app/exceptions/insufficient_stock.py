from uuid import UUID
from fastapi import HTTPException, status


class InsufficientStockException(HTTPException):
    def __init__(
        self,
        id: UUID,
    ):
        detail = (f"Inventar mit ID {id} hat nicht genug Bestand")
        super().__init__(status_code=status.HTTP_400_BAD_REQUEST, detail=detail)
