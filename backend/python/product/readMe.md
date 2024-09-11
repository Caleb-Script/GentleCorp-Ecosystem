# FastAPI Project

## Installation

Um die Projektabhängigkeiten zu installieren, führe folgenden Befehl aus:

```bash
pip install -r requirements.txt

```

```plaintext
project-root/
│
├── app/
│   ├── __init__.py                # Initialisiert das `app`-Modul
│   ├── main.py                    # Hauptanwendungsdatei für FastAPI-Routen
│   ├── crud/                      # CRUD-Operationen
│   │   ├── __init__.py
│   │   ├── read_product.py        # Lese CRUD-Operationen für Produkte
│   │   └── write_product.py       # Schreib CRUD-Operationen für Produkte
│   ├── db/                        # Datenbankkonfigurationen und -operationen
│   │   ├── __init__.py
│   │   ├── data.py                # Beispieldaten für die Datenbank
│   │   └── mongo.py               # MongoDB-Verbindungskonfiguration
│   ├── exception/                 # Ausnahmebehandlungen
│   │   ├── __init__.py
│   │   └── exceptions.py
│   ├── models/                    # Enthält die Datenmodelle
│   │   ├── __init__.py
│   │   ├── product.py             # Modelle für Produkte
│   │   └── user.py                # Modelle für Benutzer
│   ├── routers/                   # Router (Kapselung von Routen)
│   │   ├── __init__.py
│   │   ├── product_router.py      # Routen für Produkte
│   │   └── auth_router.py         # Routen für Authentifizierung
│   ├── schemas/                   # Enthält Pydantic-Schemas für Validierung
│   │   ├── __init__.py
│   │   └── product.py             # Schemas für Produkte
│   ├── security/                  # Sicherheits- und Authentifizierungslogik
│   │   ├── __init__.py
│   │   ├── security.py            # Authentifizierungs- und Autorisierungslogik
│   │   └── roles.py               # Rollenbasierte Zugriffslogik
│   └── utils/                     # Hilfsfunktionen
│       ├── __init__.py
│       └── helpers.py             # Hilfsfunktionen und Utility-Methoden
│
├── tests/                         # Tests
│   ├── __init__.py
│   ├── test_product.py            # Tests für Produkte
│   └── test_auth.py               # Tests für Authentifizierung
│
├── pyproject.toml
├── .venv_product
├── requirements.txt               # Python-Abhängigkeiten
└── README.md                      # Projektbeschreibung und Anweisungen
```

## Einrichtung und Ausführung

1. **Virtuelle Umgebung erstellen**

    ```bash
    python3 -m venv .venv_product
    ```

2. **Virtuelle Umgebung aktivieren**

   Auf Unix-ähnlichen Systemen (Linux, macOS):

    ```bash
    source .venv_product/bin/activate
    ```

   Auf Windows:

    ```bash
    .venv_product\Scripts\Activate.ps1
    ```

3. **Abhängigkeiten installieren**

    ```bash
    pip3 install -r requirements.txt
    ```

4. **Projekt starten**

    ```bash
    uvicorn app.main:app --reload
    ```
