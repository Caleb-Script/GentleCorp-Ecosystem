import pytest
import asyncio
from httpx import AsyncClient
from motor.motor_asyncio import AsyncIOMotorClient
from app.main import app
from app.db.mongo import get_database
from app.schemas import ProductModel
from app.models import ProductCategoryType

base_url = "http://localhost:8000"
username = "admin"
password = "p"
new_id = ""
query_params = {"max_price": 200, "min_price": 100}
query_params2 = {"brand": "Apple"}
product_id = "70000000-0000-0000-0000-000000000000"


@pytest.fixture(scope="session")
def event_loop():
    policy = asyncio.get_event_loop_policy()
    loop = policy.new_event_loop()
    yield loop
    loop.close()


@pytest.fixture(scope="session")
async def test_db():
    from app.core.settings import settings
    client = AsyncIOMotorClient(settings.DATABASE_URL)
    db = client[settings.DATABASE_NAME]
    yield db
    await client.drop_database(settings.DATABASE_NAME)
    client.close()


@pytest.fixture(scope="function")
async def client():
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
    return client  # Ändern Sie dies von "return client" zu "return client"


@pytest.fixture(autouse=True)
async def reset_db(test_db):
    await test_db["products"].delete_many({})


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
    assert data["category"] == "ELECTRONICS"


@pytest.mark.asyncio
async def test_list_products(admin_client):
    response = await admin_client.get("/product/")
    assert response.status_code == 200
    assert isinstance(response.json(), list)
    assert len(response.json()) == 5


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
    assert data["category"] == "ELECTRONICS"


@pytest.mark.asyncio
async def test_create_product(admin_client):
    global new_id
    product_data = {
        "name": "New Test Product",
        "brand": "Test Brand",
        "price": 199.99,
        "description": "A new test product",
        "category": "ELECTRONICS",
    }
    response = await admin_client.post("/product/", json=product_data)
    assert response.status_code == 201
    new_id = response.headers["Location"].split("/")[-1]
    assert new_id, "ID should not be empty"


@pytest.mark.asyncio
async def test_update_product(admin_client):
    global new_id
    update_data = {"price": 249.99}
    response = await admin_client.put(f"/product/{new_id}", json=update_data)
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_updated_product(admin_client):
    global new_id
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 200
    data = response.json()
    assert data["name"] == "New Test Product"
    assert data["price"] == 249.99


@pytest.mark.asyncio
async def test_delete_product(admin_client):
    global new_id
    response = await admin_client.delete(f"/product/{new_id}")
    assert response.status_code == 204


@pytest.mark.asyncio
async def test_get_deleted_product(admin_client):
    global new_id
    response = await admin_client.get(f"/product/{new_id}")
    assert response.status_code == 404
