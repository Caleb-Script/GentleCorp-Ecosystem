from fastapi import HTTPException


class VersionMissingException(HTTPException):
    def __init__(self):
        super().__init__(
            status_code=428,
            detail="The If-None-Match header is required for version control.",
        )
