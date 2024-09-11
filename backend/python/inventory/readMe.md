# FastAPI Project

## Installation

Um die Projektabhängigkeiten zu installieren, führe folgenden Befehl aus:

```bash
pip install -r requirements.txt

```

Erläuterungen zu den Verzeichnissen und Dateien:
app/main.py: Die Hauptdatei für den FastAPI-Server. Hier wird die FastAPI-Anwendung erstellt und konfiguriert.
app/models/: Beinhaltet die SQLAlchemy-Datenbankmodelle. base.py enthält die Basisklasse für alle SQLAlchemy-Modelle.
app/crud/: Beinhaltet die CRUD-Operationen für verschiedene Entitäten. Die CRUD-Operationen kapseln die Datenbankoperationen und ermöglichen eine klare Trennung von Logik und Datenzugriff.
app/db/: Konfiguriert die Verbindung zur Datenbank und verwaltet SQLAlchemy-Sitzungen. mysql.py enthält die Datenbankverbindungsdetails, während session.py die SQLAlchemy-Sitzungen verwaltet.
app/schemas/: Beinhaltet die Pydantic-Schemas für Datenvalidierung und Serialisierung. Diese definieren, wie Daten in und aus der API übertragen werden.
app/exceptions/: Beinhaltet benutzerdefinierte Ausnahmen. Dies ermöglicht eine zentralisierte Fehlerbehandlung.
app/routers/: Beinhaltet die API-Routen. Hier definieren Sie die Endpunkte und verbinden die Routen mit den CRUD-Operationen.
app/security/: Beinhaltet Sicherheits-Utilities wie JWT-Authentifizierung.
app/utils/: Beinhaltet Hilfsfunktionen, die in verschiedenen Teilen der Anwendung verwendet werden können.
app/tests/: Beinhaltet die Tests für die Anwendung. conftest.py kann Testkonfigurationen und Fixtures enthalten. test_item.py enthält die Tests für die Item-Routen.
pyproject.toml: Enthält die Projektmetadaten und Abhängigkeiten, wenn Sie Poetry verwenden.
requirements.txt: Alternativ zu pyproject.toml, wenn Sie pip für die Verwaltung der Abhängigkeiten verwenden.
Dockerfile und docker-compose.yml: Falls Sie Docker verwenden, um die Anwendung zu containerisieren und bereitzustellen.
README.md: Dokumentation des Projekts, um anderen Entwicklern eine Einführung und Anleitung zu bieten.

```plaintext
inventory_service/
│
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── core/
│   │   ├── __init__.py
│   │   └── config.py             # Configuration for FastAPI and Keycloak
│   ├── db/
│   │   ├── __init__.py
│   │   ├── base.py               # Database base configuration
│   │   └── mysql.py              # MySQL connection setup
│   ├── models/
│   │   ├── __init__.py
│   │   └── inventory.py          # SQLAlchemy models for inventory
│   ├── routers/
│   │   ├── __init__.py
│   │   └── inventory_router.py   # Routes for inventory
│   ├── security/
│   │   ├── __init__.py
│   │   └── keycloak.py           # Keycloak integration
│   ├── schemas/
│   │   ├── __init__.py
│   │   └── inventory.py          # Pydantic schemas for inventory
│   ├── utils/
│   │   ├── __init__.py
│   │   └── dependencies.py       # Dependencies for authentication
│   ├── tests/
│   │   ├── __init__.py
│   │   └── test_inventory.py     # Tests for inventory API
│   ├── exceptions/
│   │   ├── __init__.py
│   │   └── http_exceptions.py    # HTTP exceptions for errors
│   ├── repositories/
│   │   ├── __init__.py
│   │   └── inventory_repository.py  # Repository for inventory data
│   ├── services/
│   │   ├── __init__.py
│   │   └── inventory_service.py  # Service layer for inventory logic
│   └── controllers/
│       ├── __init__.py
│       └── inventory_controller.py  # Controller layer for handling API requests
|
├── .env                             # Environment variables
├── pyproject.toml           # Project metadata and dependencies
├── requirements.txt         # Python dependencies (if not using Poetry)
├── Dockerfile                # Dockerfile for containerizing the application
├── docker-compose.yml        # Docker Compose configuration (if needed)
└── README.md                 # Project documentation

```

## Einrichtung und Ausführung

1. **Virtuelle Umgebung erstellen**

    ```bash
    python3 -m venv .venv_inventory
    ```

2. **Virtuelle Umgebung aktivieren**

   Auf Unix-ähnlichen Systemen (Linux, macOS):

    ```bash
    source .venv_inventory/bin/activate
    ```

   Auf Windows:

    ```bash
    .venv_inventory\bin\Activate.ps1
    ```

3. **Abhängigkeiten installieren**

    ```bash
    pip3 install -r requirements.txt
    ```

4. **Projekt starten**

    ```bash
    uvicorn app.main:app --reload
    ```


├── alembic/
│   ├── versions/                # Hier werden die Migrationen gespeichert
│   ├── env.py                   # Alembic-Umgebungskonfiguration
│   └── script.py.mako           # Vorlage für Migrationsskripte
