from typing import Dict, List, Optional

from fastapi import HTTPException

from ..security import Role


class UnauthorizedException(HTTPException):

    def __init__(self, username: str, roles: List[Role]):

        # Standard-Fehlermeldung
        status_code = 403

        # Konvertiere die Rollenliste in eine kommagetrennte Zeichenkette
        roles_str = ", ".join([role.value for role in roles])

        # Verschiedene Konstruktorvarianten für unterschiedliche Fehlermeldungen
        detail = f"The user {username} with the role {roles_str} does not have sufficient rights to access this resource."

        # Ruft den HTTPException-Konstruktor auf
        super().__init__(status_code=status_code, detail=detail)

        # Speichert die zusätzlichen Attribute
        self.username = username
        self.role = roles
