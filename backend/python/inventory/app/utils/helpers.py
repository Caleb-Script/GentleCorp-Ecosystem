import random


def generate_sku() -> str:
    return f"SKU-{random.randint(1000, 9999)}"
