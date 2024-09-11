from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)
new_id: str = ""


def get_access_token():
    response = client.post(
        "/auth/login",  # Ensure this is the correct path for login
        json={
            "username": "admin",
            "password": "p",
        },
    )
    assert response.status_code == 200
    return response.json()["access_token"]


def test_create_product():
    global new_id  # Declare that we are using the global variable
    token = get_access_token()
    response = client.post(
        "/products/",
        headers={"Authorization": f"Bearer {token}"},
        json={
            "name": "Test Product",
            "brand": "Test Brand",
            "category": "E",  # Ensure this matches your Enum values
            "description": "Test Description",
            "price": 100.0,
        },
    )
    assert response.status_code == 201
    new_id = response.json().get("id", "")
    assert new_id != ""


def test_delete_product():
    global new_id
    if not new_id:
        test_create_product()  # Ensure the product exists before attempting to delete

    token = get_access_token()
    response = client.delete(
        f"/products/{new_id}", headers={"Authorization": f"Bearer {token}"}
    )
    assert response.status_code == 204  # Assuming the product was deleted
    response = client.get(f"/products/{new_id}")
    assert response.status_code == 404  # Ensure the product no longer exists


def test_get_product_by_id():
    response = client.get("/products/1234567890")
    assert response.status_code == 404  # Assuming the ID doesn't exist


def test_get_products():
    response = client.get("/products/")
    assert response.status_code == 200

def test_get_apple_brand():
    response = client.get("/products", params={"brand": "Apple"})
    assert response.status_code == 200
    assert len(response.json()) > 0
    products = response.json()
    for product in products:
        assert product.get("brand") == "Apple"


def test_get_products_under_70():
    token = get_access_token()
    response = client.get(
        "/products/",
        params={"max_price": 70},
    )
    assert response.status_code == 200
    products = response.json()
    for product in products:
        assert product.get("price") < 70


def test_get_product_1():
    product_id = "000000000000000000000000"
    response = client.get(f"/products/{product_id}")
    assert response.status_code == 200
    product = response.json()
    assert product.get("name") == "Organic Apples2"
