from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_read_inventories():
    response = client.get("/inventories")
    assert response.status_code == 200


def test_read_inventory():
    response = client.get("/inventories/1")
    assert response.status_code == 404  # Adjust based on actual test setup


def test_create_inventory():
    response = client.post(
        "/inventories", json={"name": "Test Item", "quantity": 10, "price": 100}
    )
    assert response.status_code == 200
    assert response.json()["name"] == "Test Item"
