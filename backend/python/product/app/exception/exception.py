from fastapi import HTTPException


class CustomError(Exception):
    def __init__(self, detail: str):
        self.detail = detail

    def __str__(self):
        return self.detail


class DuplicateKeyError(CustomError):
    def __init__(self, name: str):
        super().__init__(f"The product '{name}' is already in the database.")


class NotFoundError(CustomError):
    def __init__(self, id: str):
        super().__init__(f"The id '{id}' was not found.")
