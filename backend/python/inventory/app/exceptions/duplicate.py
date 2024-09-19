from uuid import UUID
from fastapi import HTTPException, status
from typing import List, Optional, Dict


class DuplicateException(HTTPException):

    def __init__(
        self,
        name: str,
        brand: str,
        id: UUID
    ):

        # Standard-Fehlermeldung
        status_code = status.HTTP_409_CONFLICT

        detail = f"The Inventory with name \"{name}\" of the brand \"{brand}\" already exists."
        self.duplicate_link = f"http://localhost:8001/inventory/{id}"

        # Ruft den HTTPException-Konstruktor auf
        super().__init__(status_code=status_code, detail=detail)
        
        # Speichert die zusätzlichen Attribute
        self.name = name
        self.brand = brand
