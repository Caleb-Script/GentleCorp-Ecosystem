from fastapi import HTTPException


class NoChangesDetectedException(HTTPException):
    def __init__(self):

        super().__init__(
            status_code=304,
            detail=f"Keine Änderungen am Produkt festgestellt.",
        )
