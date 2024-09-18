import pytest
import asyncio
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from app.main import app
from app.db.mysql import get_session, Base
import pytest
from app.schemas import InventoryModel, InventoryUpdate, InventoryStatusType

base_url = "http://localhost:8000"
username = "admin"
password = "p"
new_id = ""
query_params = {"max_price": 200, "min_price": 100}
query_params2 = {"sku_code": "LMN456"}
inventory_id = "80000000-0000-0000-0000-000000000001"


@pytest.fixture(scope="session")
def event_loop():
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()


@pytest.fixture(scope="session")
async def test_db_engine():
    engine = create_async_engine(
        "mysql+aiomysql://inventory-db-user:GentleCorp21.08.2024@localhost:3306/inventory-db",
        echo=True,
    )
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield engine
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)
    await engine.dispose()


@pytest.fixture(scope="function")
async def test_db(test_db_engine):
    async_session = sessionmaker(
        test_db_engine, class_=AsyncSession, expire_on_commit=False
    )
    async with async_session() as session:
        yield session
        await session.rollback()


@pytest.fixture(scope="function")
async def client(event_loop):
    async with AsyncClient(app=app, base_url="http://test") as client:
        yield client


@pytest.fixture(scope="function")
async def admin_client(client):
    response = await client.post(
        "/auth/login", json={"username": "admin", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    return client


@pytest.fixture(scope="function")
async def basic_client(client):
    response = await client.post(
        "/auth/login", json={"username": "erik", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    return client

@pytest.fixture(scope="function")
async def elite_client(client):
    response = await client.post(
        "/auth/login", json={"username": "leroy135", "password": "Leroy135"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    return client


@pytest.fixture(scope="function")
async def supreme_client(client):
    response = await client.post(
        "/auth/login", json={"username": "gentlecg99", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    return client


@pytest.fixture(autouse=True)
async def reset_db(test_db):
    yield
    await test_db.rollback()


@pytest.mark.asyncio
async def test_db_populate(admin_client):
    response = await admin_client.post("/admin/db_populate")
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_get_inventory_not_found(admin_client):
    response = await admin_client.get("/inventory/90000000-0000-0000-0000-000000000000")
    assert response.status_code == 404

@pytest.mark.asyncio
async def test_get_inventory(admin_client):
    response = await admin_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "ABC123"
    assert data["quantity"] == 50
    assert data["unit_price"] == 299.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"


@pytest.mark.asyncio
async def test_list_inventory(admin_client):
    response = await admin_client.get("inventory/")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 3

@pytest.mark.asyncio
async def test_list_inventory_2(admin_client):
    response = await admin_client.get("inventory/", params=query_params)
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 1


@pytest.mark.asyncio
async def test_list_inventory_3(admin_client):
    response = await admin_client.get("inventory/", params=query_params2)
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 1
    data = response.json()[0]
    assert data["sku_code"] == "LMN456"
    assert data["quantity"] == 200
    assert data["unit_price"] == 79.99
    assert data["status"] == "O"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000002"

@pytest.mark.asyncio
async def test_get_full_inventory(admin_client):
    response = await admin_client.get("/inventory/full/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "ABC123"
    assert data["quantity"] == 50
    assert data["unit_price"] == 299.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"
    assert data["reserved_items"][0]["quantity"] == 5
    assert (
        data["reserved_items"][0]["username"]
        == "admin"
    )
    assert data["reserved_items"][1]["quantity"] == 15
    assert (
        data["reserved_items"][1]["username"]
        == "erik"
    )


@pytest.mark.asyncio
async def test_create_inventory(admin_client):
    global new_id
    inventory_data = {
        "sku_code": "NEW123",
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000000",
    }
    response = await admin_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 201
    new_id = response.headers["Location"].split("/")[-1]
    assert new_id, "ID should not be empty"


@pytest.mark.asyncio
async def test_update_inventory(admin_client):
    global new_id
    update_data = {"quantity": 20}
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_inventory_2(admin_client):
    global new_id
    response = await admin_client.get(
        f"/inventory/{new_id}"
    )
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "NEW123"
    assert data["quantity"] == 20
    assert data["unit_price"] == 99.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"


@pytest.mark.asyncio
async def test_update_inventory_2(admin_client):
    global new_id
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_inventory_3(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "NEW123"
    assert data["quantity"] == 2
    assert data["unit_price"] == 199.99
    assert data["status"] == "D"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"


@pytest.mark.asyncio
async def test_delete_inventory(admin_client):
    global new_id
    response = await admin_client.delete(f"/inventory/{new_id}")
    print(f"Delete response: {response.status_code}, {response.text}")
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/reserved/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2
    assert data[0]["quantity"] == 5
    assert data[0]["username"] == "admin"
    assert data[1]["quantity"] == 15
    assert data[1]["username"] == "erik"


@pytest.mark.asyncio
async def test_get_reserve_items_by_username(admin_client):
    global new_id
    response = await admin_client.get(
        f"/inventory/reserve"
    )
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2
    assert data[0]["quantity"] == 5
    assert data[0]["inventory_id"] == "80000000-0000-0000-0000-000000000000"
    assert data[1]["quantity"] == 10
    assert data[1]["inventory_id"] == "80000000-0000-0000-0000-000000000001"


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username(admin_client):
    response = await admin_client.get(f"/inventory/reserve/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 5

@pytest.mark.asyncio
async def test_reserve_item(admin_client):
    global new_id
    response = await admin_client.post(f"/inventory/{inventory_id}/item", json={"quantity": 55})
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_2(admin_client):
    response = await admin_client.get(f"/inventory/reserve/{inventory_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 65


@pytest.mark.asyncio
async def test_reserve_item_leroy(elite_client):
    global new_id
    response = await elite_client.post(
        f"/inventory/{inventory_id}/item", json={"quantity": 20}
    )
    assert response.status_code == 200


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_3(elite_client):
    response = await elite_client.get(
        f"/inventory/reserve/{inventory_id}"
    )
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 20


@pytest.mark.asyncio
async def test_reserve_item_erik(basic_client):
    global new_id
    response = await basic_client.post(
        f"/inventory/{inventory_id}/item", json={"quantity": 20}
    )
    assert response.status_code == 403
    data = response.json()
    assert data["detail"] == "Not enough permissions"


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_4(basic_client):
    response = await basic_client.get(
        f"/inventory/reserve/{inventory_id}"
    )
    assert response.status_code == 404
    data = response.json()
    assert (
        data["detail"]
        == "Keine Reservierung für den Kunden mit den Username: erik für das Inventar mit der ID: 80000000-0000-0000-0000-000000000001 gefunden!"
    )


@pytest.mark.asyncio
async def test_reserve_item_Caleb(supreme_client):
    global new_id
    response = await supreme_client.post(
        f"/inventory/{inventory_id}/item", json={"quantity": 20}
    )
    assert response.status_code == 400
    data = response.json()
    assert (
        data["detail"]
        == "Inventar mit ID 80000000-0000-0000-0000-000000000001 hat nicht genug Bestand"
    )


@pytest.mark.asyncio
async def test_reserve_item_Caleb(supreme_client):
    global new_id
    response = await supreme_client.post(
        f"/inventory/{inventory_id}/item", json={"quantity": 15}
    )
    assert response.status_code == 200


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_5(supreme_client):
    response = await supreme_client.get(
        f"/inventory/reserve/{inventory_id}"
    )
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 15
