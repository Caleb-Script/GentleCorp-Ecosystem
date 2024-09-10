from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_create_product():
    response = client.post(
        "/products/",
        json={
            "name": "Test Product",
            "brand": "Test Brand",
            "category": "ELECTRONICS",
            "price": 100.0,
        },
    )
    assert response.status_code == 201
    assert "id" in response.json()


def test_get_product_by_id():
    response = client.get("/products/1234567890")
    assert response.status_code == 404  # Assuming the ID doesn't exist


def test_get_products():
    response = client.get("/products/")
    assert response.status_code == 200
