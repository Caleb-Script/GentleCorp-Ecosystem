from pydantic import BaseModel
from typing import List

class Movie(BaseModel):
    name: str
    plot: str
    genres: List[str]
    casts: List[str]
