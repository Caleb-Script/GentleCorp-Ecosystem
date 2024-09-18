from fastapi import HTTPException


class CustomError(Exception):
    def __init__(self, detail: str):
        self.detail = detail

    def __str__(self):
        return self.detail


class UnauthorizedError(CustomError):
    def __init__(self, detail: str):
        super().__init__(detail)
