from fastapi import HTTPException, status

class InventoryExistsException(HTTPException):
    def __init__(self, id: str = None):
        detail = (
            "Inventar existiert bereits"
            if id is None
            else f"Inventar mit ID {id} existiert bereits"
        )
        super().__init__(status_code=status.HTTP_409_CONFLICT, detail=detail)


class UnzureichenderBestandFehler(HTTPException):
    def __init__(
        self,
        id: str = None,
    ):
        detail = (
            "Die verfügbare Menge im Bestand ist geringer als die angeforderte Menge"
            if id is None
            else f"Inventar mit ID {id} hat nicht genug Bestand"
        )
        super().__init__(status_code=status.HTTP_400_BAD_REQUEST, detail=detail)
