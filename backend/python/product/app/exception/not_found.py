from fastapi import HTTPException
from typing import List, Optional, Dict
from uuid import UUID


class NotFoundException(HTTPException):
    def __init__(
        self,
        id: Optional[UUID] = None,
        search_criteria: Optional[Dict[str, List[str]]] = None,
    ):
        # Standard-Fehlermeldung
        status_code = 404

        # Verschiedene Konstruktorvarianten für unterschiedliche Fehlermeldungen
        if id:
            detail = f"Keinen Kunden mit der ID {id} gefunden."
        elif search_criteria:
            detail = f"Keine Kunden mit diesen Suchkriterien gefunden: {search_criteria}"
        else:
            detail = "Keine Kunden gefunden."

        # Ruft den HTTPException-Konstruktor auf
        super().__init__(status_code=status_code, detail=detail)

        # Speichert die zusätzlichen Attribute
        self.id = id
        self.search_criteria = search_criteria
