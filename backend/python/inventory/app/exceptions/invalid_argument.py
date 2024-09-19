from fastapi import HTTPException


class InvalidArgumentException(HTTPException):
    def __init__(
        self,
        version: str,
    ):

        detail = f"{version} ist eine ungültige versionsnummer"
        status_code = 412
        super().__init__(status_code=status_code, detail=detail)

        self.version = version
