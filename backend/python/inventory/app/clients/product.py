from pydantic import BaseModel
from typing import Dict, Any


class ProductInfo(BaseModel):
    name: str
    brand: str

    @classmethod
    def to_product_info(cls, json_data: Dict[str, Any]) -> 'ProductInfo':
        """
        Konvertiert ein JSON-Objekt in ein ProductInfo-Objekt.
        
        :param json_data: Ein Dictionary, das die JSON-Daten repräsentiert
        :return: Ein ProductInfo-Objekt
        """
        return cls(
            name=json_data.get('name', ''),
            brand=json_data.get('brand', '')
        )
