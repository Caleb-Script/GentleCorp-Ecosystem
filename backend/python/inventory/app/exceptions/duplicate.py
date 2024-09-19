from fastapi import HTTPException
from typing import List, Optional, Dict


class DuplicateException(HTTPException):

    def __init__(
        self,
        name: str,
        brand: str,
    ):

        # Standard-Fehlermeldung
        status_code = 409

        detail = f"The Inventory with name \"{name}\" of the brand \"{brand}\" already exists."

        # Ruft den HTTPException-Konstruktor auf
        super().__init__(status_code=status_code, detail=detail)

        # Speichert die zusätzlichen Attribute
        self.name = name
        self.brand = brand

