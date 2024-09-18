from fastapi import HTTPException


class InvalidException(HTTPException):
    def __init__(self, version: str,):

        detail = ("%s ist eine ungültige versionsnummer", version)
        status_code = 400
        super().__init__(status_code=status_code, detail=detail)

        self.version = version