from fastapi import HTTPException


class NoChangesException(HTTPException):
    def __init__(self):

        super().__init__(
            status_code=304,
            detail=f"Keine Änderungen am Produkt festgestellt.",
        )
