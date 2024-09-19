import pytest
import asyncio
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker
from app.main import app
from app.db.mysql import Base

base_url = "http://localhost:8000"
username = "admin"
password = "p"
new_id = ""

inventory_id_1 = "80000000-0000-0000-0000-000000000001"


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
    client.headers["If-None-Match"] = "-1"
    client.headers["If-Match"] = "0"
    return client


@pytest.fixture(scope="function")
async def user_client(client):
    response = await client.post(
        "/auth/login", json={"username": "user", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    client.headers["If-None-Match"] = "-1"
    client.headers["If-Match"] = "0"
    return client


@pytest.fixture(scope="function")
async def basic_client(client):
    response = await client.post(
        "/auth/login", json={"username": "erik", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    client.headers["If-None-Match"] = "-1"
    client.headers["If-Match"] = "0"
    return client

@pytest.fixture(scope="function")
async def elite_client(client):
    response = await client.post(
        "/auth/login", json={"username": "leroy135", "password": "Leroy135"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    client.headers["If-None-Match"] = "-1"
    client.headers["If-Match"] = "0"
    return client

@pytest.fixture(scope="function")
async def supreme_client(client):
    response = await client.post(
        "/auth/login", json={"username": "gentlecg99", "password": "p"}
    )
    assert response.status_code == 200
    token = response.json()["access_token"]
    client.headers["Authorization"] = f"Bearer {token}"
    client.headers["If-None-Match"] = "-1"
    client.headers["If-Match"] = "0"
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
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "Kein Inventar mit der ID: 90000000-0000-0000-0000-000000000000 gefunden!"
    )

@pytest.mark.asyncio
async def test_get_inventory_0_admin(admin_client):
    response = await admin_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "ABC123"
    assert data["quantity"] == 50
    assert data["unit_price"] == 299.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"
    assert data["name"] == "IPhone 14"
    assert data["brand"] == "Apple"
    assert data["version"] == 0


@pytest.mark.asyncio
async def test_get_inventory_0_not_modified(admin_client):
    admin_client.headers["If-None-Match"] = "0"
    response = await admin_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 304


@pytest.mark.asyncio
async def test_get_inventory_0_invalid_version(admin_client):
    admin_client.headers["If-None-Match"] = "1w"
    response = await admin_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 412
    assert "message" in response.json()
    assert response.json()["message"] == "1w ist eine ungültige versionsnummer"


@pytest.mark.asyncio
async def test_get_inventory_0_missing_version(admin_client):
    del admin_client.headers["If-None-Match"]
    response = await admin_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 428
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The If-None-Match header is required for version control."
    )


@pytest.mark.asyncio
async def test_get_inventory_0_user(user_client):
    response = await user_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["sku_code"] == "ABC123"
    assert data["quantity"] == 50
    assert data["unit_price"] == 299.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000000"
    assert data["name"] == "IPhone 14"
    assert data["brand"] == "Apple"
    assert data["version"] == 0

@pytest.mark.asyncio
async def test_get_inventory_0_basic(basic_client):
    response = await basic_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )

@pytest.mark.asyncio
async def test_get_inventory_0_elite(elite_client):
    response = await elite_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."
    )

@pytest.mark.asyncio
async def test_get_inventory_0_supreme(supreme_client):
    response = await supreme_client.get("/inventory/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."
    )

@pytest.mark.asyncio
async def test_get_inventory_list_as_admin(admin_client):
    response = await admin_client.get("inventory/")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 3


@pytest.mark.asyncio
async def test_get_inventory_list_as_user(user_client):
    response = await user_client.get("inventory/")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 3


@pytest.mark.asyncio
async def test_get_inventory_list_as_supreme(supreme_client):
    response = await supreme_client.get("inventory/")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_get_inventory_list_as_elite(elite_client):
    response = await elite_client.get("inventory/")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."
    )

@pytest.mark.asyncio
async def test_get_inventory_list_as_basic(basic_client):
    response = await basic_client.get("inventory/")
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )

@pytest.mark.asyncio
async def test_get_inventory_list_not_authorized(client):
    response = await client.get("inventory/")
    assert response.status_code == 401
    assert "detail" in response.json()
    assert response.json()["detail"] == "Authorization header missing"

query_params = {"max_price": 200, "min_price": 100}

@pytest.mark.asyncio
async def test_get_inventory_list_with_query_params(admin_client):
    response = await admin_client.get("inventory/", params=query_params)
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 1
    data = response.json()[0]
    assert data["sku_code"] == "XYZ789"
    assert data["version"] == 0
    assert data["quantity"] == 100
    assert data["unit_price"] == 149.99
    assert data["status"] == "D"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000001"
    assert data["name"] == "Bio-Äpfel"
    assert data["brand"] == "Bauern's Beste"

query_params_3 = {"product_id": "80000000-0000-0000-0000-000000000000"}
@pytest.mark.asyncio
async def test_list_inventory_not_found(admin_client):
    response = await admin_client.get("inventory/", params=query_params_3)
    assert response.status_code == 404
    assert "message" in response.json()
    assert response.json()["message"] == "Keine Inventare gefunden."

query_params_2 = {"sku_code": "LMN456"}


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
    assert data["name"] == "IPhone 14"
    assert data["brand"] == "Apple"
    assert data["version"] == 0
    assert data["reserved_items"][0]["quantity"] == 5
    assert data["reserved_items"][0]["version"] == 0
    assert data["reserved_items"][0]["username"] == "admin"
    assert data["reserved_items"][0]["id"] == "90000000-0000-0000-0000-000000000000"

    assert data["reserved_items"][1]["quantity"] == 15
    assert data["reserved_items"][1]["version"] == 0
    assert data["reserved_items"][1]["username"] == "gentlecg99"
    assert data["reserved_items"][1]["id"] == "90000000-0000-0000-0000-000000000002"


@pytest.mark.asyncio
async def test_create_inventory(admin_client):
    global new_id
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000004",
    }
    response = await admin_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 201
    new_id = response.headers["Location"].split("/")[-1]
    assert new_id, "ID should not be empty"

@pytest.mark.asyncio
async def test_create_inventory_as_user(user_client):
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000004",
    }
    response = await user_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 403
    assert response.json()["message"] == "The user user with the role USER does not have sufficient rights to access this resource."


@pytest.mark.asyncio
async def test_create_inventory_as_supreme(supreme_client):
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000004",
    }
    response = await supreme_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 403
    assert response.json()["message"] == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."


@pytest.mark.asyncio
async def test_create_inventory_as_elite(elite_client):
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000004",
    }
    response = await elite_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 403
    assert response.json()["message"] == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."


@pytest.mark.asyncio
async def test_create_inventory_as_basic(basic_client):
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000004",
    }
    response = await basic_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 403
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_create_inventory_produkt_gibt_es_nicht(admin_client):
    global new_id
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "00000000-0000-0000-0000-000000000000",
    }
    response = await admin_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 404
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "Kein Produkt mit der ID: 00000000-0000-0000-0000-000000000000 gefunden"
    )


@pytest.mark.asyncio
async def test_create_inventory_duplicate(admin_client):
    global new_id
    inventory_data = {
        "quantity": 10,
        "unit_price": 99.99,
        "status": "A",
        "product_id": "70000000-0000-0000-0000-000000000000",
    }
    response = await admin_client.post("/inventory/", json=inventory_data)
    assert response.status_code == 409
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == 'The Inventory with name "IPhone 14" of the brand "Apple" already exists.'
    )
    assert (
        response.json()["_links"]["duplicate"]["href"]
        == "http://localhost:8001/inventory/80000000-0000-0000-0000-000000000000"
    )


@pytest.mark.asyncio
async def test_get_new_inventory(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert len(data["sku_code"]) == 11
    assert data["quantity"] == 10
    assert data["unit_price"] == 99.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000004"
    assert data["name"] == "Lego Star Wars Set"
    assert data["brand"] == "LEGO"
    assert data["version"] == 0


@pytest.mark.asyncio
async def test_update_inventory_with_old_version(admin_client):
    global new_id
    update_data = {"quantity": 20}
    admin_client.headers["If-Match"] = "-1"
    response = await admin_client.put(
        f"/inventory/80000000-0000-0000-0000-000000000000", json=update_data
    )
    assert response.status_code == 409
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "Version conflict for product 80000000-0000-0000-0000-000000000000. Current version is 0, but version -1 was requested."
    )

@pytest.mark.asyncio
async def test_partial_update_new_inventory(admin_client):
    global new_id
    update_data = {"quantity": 20}
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_update_new_inventory_no_changes(admin_client):
    global new_id
    update_data = {"quantity": 20}
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 304


@pytest.mark.asyncio
async def test_update_new_inventory_unchangenable(admin_client):
    global new_id
    update_data = {"sku_code": "ASD", "product_id": "new-Id"}
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 304


@pytest.mark.asyncio
async def test_update_new_inventory_no_input(admin_client):
    global new_id
    update_data = {}
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 304


@pytest.mark.asyncio
async def test_get_new_updated_inventory(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert len(data["sku_code"]) == 11
    assert data["quantity"] == 20
    assert data["unit_price"] == 99.99
    assert data["status"] == "A"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000004"
    assert data["name"] == "Lego Star Wars Set"
    assert data["brand"] == "LEGO"
    assert data["version"] == 1


@pytest.mark.asyncio
async def test_update_inventory_as_user(user_client):
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    response = await user_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 403
    assert (
        response.json()["message"]
        == "The user user with the role USER does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_update_inventory_as_supreme(supreme_client):
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    response = await supreme_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 403
    assert (
        response.json()["message"]
        == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_update_inventory_as_elite(elite_client):
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    response = await elite_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 403
    assert (
        response.json()["message"]
        == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_update_inventory_as_basic(basic_client):
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    response = await basic_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 403
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_update_new_inventory(admin_client):
    global new_id
    update_data = {"quantity": 2, "status": "D", "unit_price": 199.99}
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.put(f"/inventory/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_updatet_inventory_version_2(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 2
    assert data["unit_price"] == 199.99
    assert data["status"] == "D"
    assert data["product_id"] == "70000000-0000-0000-0000-000000000004"
    assert data["name"] == "Lego Star Wars Set"
    assert data["brand"] == "LEGO"
    assert data["version"] == 2


@pytest.mark.asyncio
async def test_delete_inventory_as_user(user_client):
    global new_id
    response = await user_client.delete(
        f"/inventory/80000000-0000-0000-0000-000000000002"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user user with the role USER does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_delete_inventory_as_supreme(supreme_client):
    global new_id
    response = await supreme_client.delete(
        f"/inventory/80000000-0000-0000-0000-000000000002"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_delete_inventory_as_elite(elite_client):
    global new_id
    response = await elite_client.delete(
        f"/inventory/80000000-0000-0000-0000-000000000002"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_delete_inventory_as_basic(basic_client):
    global new_id
    response = await basic_client.delete(
        f"/inventory/80000000-0000-0000-0000-000000000002"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_delete_inventory(admin_client):
    response = await admin_client.delete(f"/inventory/80000000-0000-0000-0000-000000000002")
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_delete_new_inventory(admin_client):
    global new_id
    admin_client.headers["If-Match"] = "2"
    response = await admin_client.delete(f"/inventory/{new_id}")
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory(admin_client):
    global new_id
    response = await admin_client.get(f"/inventory/reserved/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2
    assert data[0]["quantity"] == 5
    assert data[0]["version"] == 0
    assert data[0]["username"] == "admin"
    assert data[0]["id"] == "90000000-0000-0000-0000-000000000000"

    assert data[1]["quantity"] == 15
    assert data[1]["version"] == 0
    assert data[1]["username"] == "gentlecg99"
    assert data[1]["id"] == "90000000-0000-0000-0000-000000000002"


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory_as_user(user_client):
    global new_id
    response = await user_client.get(
        f"/inventory/reserved/80000000-0000-0000-0000-000000000000"
    )
    assert response.status_code == 200
    data = response.json()
    assert len(data) == 2
    assert data[0]["quantity"] == 5
    assert data[0]["version"] == 0
    assert data[0]["username"] == "admin"
    assert data[0]["id"] == "90000000-0000-0000-0000-000000000000"

    assert data[1]["quantity"] == 15
    assert data[1]["version"] == 0
    assert data[1]["username"] == "gentlecg99"
    assert data[1]["id"] == "90000000-0000-0000-0000-000000000002"


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory_as_supreme(supreme_client):
    global new_id
    response = await supreme_client.get(
        f"/inventory/reserved/80000000-0000-0000-0000-000000000000"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user gentlecg99 with the role SUPREME does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory_as_elite(elite_client):
    global new_id
    response = await elite_client.get(
        f"/inventory/reserved/80000000-0000-0000-0000-000000000000"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user leroy135 with the role ELITE does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_get_reserve_items_by_inventory_as_basic(basic_client):
    global new_id
    response = await basic_client.get(
        f"/inventory/reserved/80000000-0000-0000-0000-000000000000"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


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
    assert data[0]["version"] == 0
    assert data[1]["quantity"] == 10
    assert data[1]["inventory_id"] == "80000000-0000-0000-0000-000000000001"
    assert data[1]["version"] == 0


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username(admin_client):
    response = await admin_client.get(f"/inventory/reserve/80000000-0000-0000-0000-000000000000")
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 5
    assert data["version"] == 0

@pytest.mark.asyncio
async def test_reserve_item(admin_client):
    global new_id
    response = await admin_client.post(f"/inventory/{inventory_id_1}/item", json={"quantity": 55})
    assert response.status_code == 201

@pytest.mark.asyncio
async def test_get_updated_reserve_item_by_inventory_and_username(admin_client):
    response = await admin_client.get(f"/inventory/reserve/{inventory_id_1}")
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 65


@pytest.mark.asyncio
async def test_reserve_item_as_elite(elite_client):
    global new_id
    response = await elite_client.post(
        f"/inventory/{inventory_id_1}/item", json={"quantity": 20}
    )
    assert response.status_code == 201


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_as_elite(elite_client):
    response = await elite_client.get(
        f"/inventory/reserve/{inventory_id_1}"
    )
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 20


@pytest.mark.asyncio
async def test_reserve_item_as_basic(basic_client):
    global new_id
    response = await basic_client.post(
        f"/inventory/{inventory_id_1}/item", json={"quantity": 20}
    )
    assert response.status_code == 403
    data = response.json()
    assert (
        data["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_as_basic(basic_client):
    response = await basic_client.get(
        f"/inventory/reserve/{inventory_id_1}"
    )
    assert response.status_code == 403
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The user erik with the role BASIC does not have sufficient rights to access this resource."
    )


@pytest.mark.asyncio
async def test_reserve_item_as_supreme_insufficient_stock(supreme_client):
    global new_id
    response = await supreme_client.post(
        f"/inventory/{inventory_id_1}/item", json={"quantity": 20}
    )
    assert response.status_code == 400
    data = response.json()
    assert (
        data["message"]
        == "Inventar mit ID 80000000-0000-0000-0000-000000000001 hat nicht genug Bestand"
    )


@pytest.mark.asyncio
async def test_reserve_item_as_supreme(supreme_client):
    global new_id
    response = await supreme_client.post(
        f"/inventory/{inventory_id_1}/item", json={"quantity": 15}
    )
    assert response.status_code == 201


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_as_supreme(supreme_client):
    response = await supreme_client.get(
        f"/inventory/reserve/{inventory_id_1}"
    )
    assert response.status_code == 200
    data = response.json()
    assert data["quantity"] == 15


@pytest.mark.asyncio
async def test_get_reserve_item_by_inventory_and_username_as_supreme_no_reservation(supreme_client):
    response = await supreme_client.get(f"/inventory/reserve/80000000-0000-0000-0000-000000000002")
    assert response.status_code == 404
    data = response.json()
    assert (
        data["message"]
        == "Keine Reservierung für den Kunden mit dem Username: gentlecg99 für das Inventar mit der ID: 80000000-0000-0000-0000-000000000002 gefunden!"
    )
