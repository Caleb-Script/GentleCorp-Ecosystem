# app/tests/test_db.py
from app.mysql import engine, Base, SessionLocal
from app.inventory import Inventory


def test_db_connection():
    # Create tables
    Base.metadata.create_all(bind=engine)

    # Create a new session
    with SessionLocal() as session:
        # Add a test record
        new_item = Inventory(name="Test Item")
        session.add(new_item)
        session.commit()

        # Query the record
        result = session.query(Inventory).filter_by(name="Test Item").first()
        assert result is not None
        assert result.name == "Test Item"
