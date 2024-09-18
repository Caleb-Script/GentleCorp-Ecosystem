import pytest
import asyncio
from httpx import AsyncClient
from motor.motor_asyncio import AsyncIOMotorClient
from app.main import app
from app.core.settings import settings

base_url = "http://localhost:8000"
username = "admin"
password = "p"
new_id = ""
query_params = {"max_price": 600, "min_price": 100}
query_params2 = {"brand": "Apple"}
product_id = "70000000-0000-0000-0000-000000000000"
product_id_2 = "70000000-0000-0000-0000-000000000001"


@pytest.fixture(scope="session")
def event_loop():
    policy = asyncio.get_event_loop_policy()
    loop = policy.new_event_loop()
    yield loop
    loop.close()


@pytest.fixture(scope="function")
async def test_db():
    client = AsyncIOMotorClient(settings.DATABASE_URL)
    db = client[settings.DATABASE_NAME]
    yield db
    # await client.drop_database(settings.DATABASE_NAME)
    # client.close()


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

@pytest.fixture(autouse=True)
async def reset_db(test_db):
    await test_db["product"].delete_many({})  # Ändern Sie dies zu der korrekten Collection


@pytest.mark.asyncio
async def test_db_populate(admin_client):
    response = await admin_client.post("/admin/db_populate")
    assert response.status_code == 200


@pytest.mark.asyncio
async def test_get_product_not_found(admin_client):
    response = await admin_client.get("/product/90000000-0000-0000-0000-000000000000")
    assert response.status_code == 404


@pytest.mark.asyncio
async def test_get_product(admin_client):
    response = await admin_client.get(f"/product/{product_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "Apple iPhone 14"
    assert data["brand"] == "Apple"
    assert data["price"] == 999.99
    assert data["category"] == "E"


@pytest.mark.asyncio
async def test_get_product_ohne_version(admin_client):
    if "If-None-Match" in admin_client.headers:
        del admin_client.headers["If-None-Match"]
    response = await admin_client.get(f"/product/{product_id}")
    assert response.status_code == 428
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The If-None-Match header is required for version control."
    )


@pytest.mark.asyncio
async def test_get_product_aktuelle_version(admin_client):
    admin_client.headers["If-None-Match"] = "0"
    response = await admin_client.get(f"/product/{product_id}")
    assert response.status_code == 304


@pytest.mark.asyncio
async def test_get_product_ungültige_version(admin_client):
    admin_client.headers["If-None-Match"] = "0a"
    response = await admin_client.get(f"/product/{product_id}")
    assert response.status_code == 412
    assert "message" in response.json()
    assert response.json()["message"] == "0a ist eine ungültige versionsnummer"


@pytest.mark.asyncio
async def test_list_products_with_price_range(admin_client):
    response = await admin_client.get("/product/", params=query_params)
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 1


@pytest.mark.asyncio
async def test_list_products_by_brand(admin_client):
    response = await admin_client.get("/product/", params=query_params2)
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 1
    data = response.json()[0]
    assert data["name"] == "Apple iPhone 14"
    assert data["brand"] == "Apple"
    assert data["price"] == 999.99
    assert data["category"] == "E"


@pytest.mark.asyncio
async def test_create_product(admin_client):
    global new_id
    product_data = {
        "name": "New Test Product",
        "brand": "Test Brand",
        "price": 199.99,
        "description": "A new test product",
        "category": "E",
    }
    response = await admin_client.post("/product/", json=product_data)
    assert response.status_code == 201
    new_id = response.headers["Location"].split("/")[-1]
    assert new_id, "ID should not be empty"


@pytest.mark.asyncio
async def test_update_product_ohne_version(admin_client):
    global new_id
    update_data = {"price": 249.99}
    del admin_client.headers["If-Match"]
    response = await admin_client.put(f"/product/{new_id}", json=update_data)
    assert response.status_code == 428
    assert "message" in response.json()
    assert response.json()["message"] == "The If-None-Match header is required for version control."


@pytest.mark.asyncio
async def test_update_product_falscher_version(admin_client):
    global new_id
    update_data = {"price": 249.99}
    admin_client.headers["If-Match"] = "-1"
    response = await admin_client.put(f"/product/{new_id}", json=update_data)
    assert response.status_code == 409
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == f"Version conflict for product {new_id}. Current version is 0, but version -1 was requested.",
    )


@pytest.mark.asyncio
async def test_update_product(admin_client):
    global new_id
    update_data = {"price": 249.99}
    response = await admin_client.put(f"/product/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_updated_product_old_version(admin_client):
    global new_id
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "New Test Product"
    assert data["price"] == 249.99


@pytest.mark.asyncio
async def test_delete_product_old_version(admin_client):
    global new_id
    response = await admin_client.delete(f"/product/{new_id}")
    assert response.status_code == 409


@pytest.mark.asyncio
async def test_get_updated_product(admin_client):
    global new_id
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "New Test Product"
    assert data["price"] == 249.99


@pytest.mark.asyncio
async def test_get_updated_product_ohne_änderung(admin_client):
    global new_id
    admin_client.headers["If-Match"] = "2"
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 200


@pytest.mark.asyncio
async def test_delete_product(admin_client):
    global new_id
    admin_client.headers["If-Match"] = "1"
    response = await admin_client.delete(f"/product/{new_id}")
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_deleted_product(admin_client):
    global new_id
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 404

@pytest.mark.asyncio
async def test_create_duplicate_product(admin_client):
    product_data = {
        "name": "Apple iPhone 14",
        "brand": "Apple",
        "price": 999.99,
        "description": "Neuestes iPhone mit fortschrittlicher Kamera und Display.",
        "category": "E",
    }
    response = await admin_client.post("/product/", json=product_data)
    assert response.status_code == 409
    assert "message" in response.json()
    assert (
        response.json()["message"]
        == "The Product with name \"Apple iPhone 14\" of the brand \"Apple\" already exists."
    )

@pytest.mark.asyncio
async def test_unauthorized_access(basic_client):
    response = await basic_client.get("/product/")
    assert response.status_code == 403
    assert "message" in response.json()
    assert response.json()["message"] == "The user erik with the role BASIC does not have sufficient rights to access this resource."  

@pytest.mark.asyncio
async def test_unauthorized_access_2(client):
    response = await client.get("/product/")
    assert response.status_code == 401

@pytest.mark.asyncio
async def test_user_access_product_list(user_client):
    response = await user_client.get("/product/")
    assert response.status_code == 200

@pytest.mark.asyncio
async def test_basic_access(basic_client):
    response = await basic_client.get("/product/")
    assert response.status_code == 403
    assert "message" in response.json()
    assert response.json()["message"] == "The user erik with the role BASIC does not have sufficient rights to access this resource."

@pytest.mark.asyncio
async def test_create_product_with_basic_role(basic_client):
    product_data = {
        "name": "New Test Product",
        "brand": "Test Brand",
        "price": 199.99,
        "description": "A new test product",
        "category": "E",
    }
    response = await basic_client.post("/product/", json=product_data)
    assert response.status_code == 403

@pytest.mark.asyncio
async def test_update_product_with_basic_role(basic_client):
    update_data = {"price": 249.99}
    response = await basic_client.put(f"/product/{product_id_2}", json=update_data)
    assert response.status_code == 403

@pytest.mark.asyncio
async def test_delete_product_with_basic_role(basic_client):
    response = await basic_client.delete(f"/product/{product_id_2}")
    assert response.status_code == 403


@pytest.mark.asyncio
async def test_create_product_with_user_role(user_client):
    product_data = {
        "name": "New Test Product",
        "brand": "Test Brand",
        "price": 199.99,
        "description": "A new test product",
        "category": "E",
    }
    response = await user_client.post("/product/", json=product_data)
    assert response.status_code == 403


@pytest.mark.asyncio
async def test_update_product_with_user_role(user_client):
    update_data = {"price": 249.99}
    response = await user_client.put(f"/product/{product_id_2}", json=update_data)
    assert response.status_code == 204

@pytest.mark.asyncio
async def test_get_product_2_with_user(user_client):
    response = await user_client.get(f"/product/{product_id_2}")
    assert response.status_code == 200
    assert response.json()["price"] == 249.99


@pytest.mark.asyncio
async def test_delete_product_with_user_role(user_client):
    response = await user_client.delete(f"/product/{product_id_2}")
    assert response.status_code == 403
