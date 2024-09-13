import asyncio
from fastapi.testclient import TestClient
import pytest
from httpx import AsyncClient, ASGITransport
from app.main import app

new_id: str = ""
inventory_url = "http://localhost:8000/inventory/"
admin_url = "http://localhost:8000/admin/"


@pytest.fixture(scope="session")
def event_loop():
    # Create a new event loop for the session
    loop = asyncio.new_event_loop()
    yield loop
    loop.close()


@pytest.mark.asyncio
async def test_db_populate():
   async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
       response = await async_client.post(f"{admin_url}db_populate")
   assert response.status_code == 200

@pytest.mark.asyncio
async def test_get_by_id_200():
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.get(
            f"{inventory_url}10000000-0000-0000-0000-000000000000"
        )
        assert response.status_code == 200


@pytest.mark.asyncio
async def test_get_by_id_404():
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.get(f"{inventory_url}1234567890")
    assert response.status_code == 404


@pytest.mark.asyncio
async def test_get_inventory_by_id():
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.get(
            f"{inventory_url}10000000-0000-0000-0000-000000000000"
        )
    assert response.status_code == 200
    assert response.json() == {
        "sku_code": "ABC123",
        "quantity": 50,
        "unit_price": 299.99,
        "status": "A",
        "product_id": "00000000-0000-0000-0000-000000000000",
    }


@pytest.mark.asyncio
async def test_list_inventory():
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.get(f"{inventory_url}")
    assert response.status_code == 200
    assert isinstance(response.json(), list)


@pytest.mark.asyncio
async def test_create_inventory():
    global new_id  # Declare that we are using the global variable
    inventory_data = {
        "sku_code": "NEW123",
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "00000000-0000-0000-0000-000000000001",
    }
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.post(
            f"{inventory_url}", json=inventory_data
        )
        print(response.text)
        assert response.status_code == 201

        # Extract id from the Location header
        location_header = response.headers.get("Location")
        assert location_header is not None

        # Extract the id from the URL
        new_id = location_header.split("/")[-1]
        assert new_id != ""


@pytest.mark.asyncio
async def test_update_inventory():
    global new_id
    if not new_id:
        test_create_inventory()

    new_quantity = 60
    inventory_data = {"quantity": new_quantity}
    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        update_response = await async_client.put(f"{inventory_url}{new_id}", json=inventory_data)
        get_response    = await async_client.get(f"{inventory_url}{new_id}")
        assert update_response.status_code == 200
        assert get_response.status_code == 200
        assert get_response.json() == {
            "sku_code": "NEW123",
            "quantity": new_quantity,
            "unit_price": 99.99,
            "status": "A",
            "product_id": "00000000-0000-0000-0000-000000000001",
        }


@pytest.mark.asyncio
async def test_delete_inventory():
    global new_id
    if not new_id:
        test_create_inventory()

    async with AsyncClient(transport=ASGITransport(app=app)) as async_client:
        response = await async_client.get(f"{inventory_url}{new_id}")
        assert response.status_code == 200
