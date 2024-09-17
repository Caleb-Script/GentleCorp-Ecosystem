from fastapi import HTTPException
from typing import List, Optional, Dict
from uuid import UUID


class NotFoundException(HTTPException):
    def __init__(
        self,
        username: Optional[str] = None,
        id: Optional[UUID] = None,
        search_criteria: Optional[Dict[str, List[str]]] = None,
    ):
        # Standard-Fehlermeldung
        status_code = 404

        # Verschiedene Konstruktorvarianten für unterschiedliche Fehlermeldungen
        if username and id is None:
            detail = f"Keine Reservierung für den Kunden mit den Username: {username} gefunden!"
        elif id and username is None:
            detail = f"Kein Inventar mit der ID: {id} gefunden!"
        elif id and username:
            detail = (
                f"Keine Reservierung für den Kunden mit den Username: {username} für das Inventar mit der ID: {id} gefunden!"
            )
        elif search_criteria:
            detail = (
                f"Keine Inventare mit den Suchkriterien {search_criteria} gefunden!"
            )
        else:
            detail = "Keine Inventare gefunden."

        # Ruft den HTTPException-Konstruktor auf
        super().__init__(status_code=status_code, detail=detail)
