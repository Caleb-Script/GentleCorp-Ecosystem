from fastapi import HTTPException
from typing import List, Optional, Dict
from uuid import UUID


class NotFoundException(HTTPException):
    def __init__(
        self,
        username: Optional[str] = None,
        id: Optional[UUID] = None,
        search_criteria: Optional[Dict[str, List[str]]] = None,
        product_id: Optional[UUID] = None,
    ):
        status_code = 404

        if product_id:
            detail = f"Kein Produkt mit der ID: {product_id} gefunden"
        elif username and id is None:
            detail = f"Keine Reservierung für den Kunden mit dem Username: {username} gefunden!"
        elif id and username is None:
            detail = f"Kein Inventar mit der ID: {id} gefunden!"
        elif id and username:
            detail = f"Keine Reservierung für den Kunden mit dem Username: {username} für das Inventar mit der ID: {id} gefunden!"
        elif search_criteria:
            detail = f"Keine Inventare mit den Suchkriterien {search_criteria} gefunden!"
        else:
            detail = "Keine Inventare gefunden."

        super().__init__(status_code=status_code, detail=detail)
