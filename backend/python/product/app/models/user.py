from typing import List
from pydantic import BaseModel


class LoginDTO(BaseModel):
    username: str
    password: str


class User(BaseModel):
    sub: str
    username: str
    roles: List[str]
