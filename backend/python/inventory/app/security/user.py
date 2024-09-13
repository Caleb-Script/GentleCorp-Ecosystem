from typing import List
from pydantic import BaseModel
from enum import Enum


class Role(str, Enum):
    ADMIN = "ADMIN"
    USER = "USER"
    SUPREME = "SUPREME"
    ELITE = "ELITE"
    BASIC = "BASIC"

    @classmethod
    def has_value(cls, value):
        return value in cls._value2member_map_


class LoginDTO(BaseModel):
    username: str
    password: str


class User(BaseModel):
    sub: str
    username: str
    roles: List[Role]
