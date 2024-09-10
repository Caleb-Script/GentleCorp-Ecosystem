# FastAPI Project

## Installation

To install the project dependencies, run:

```bash
pip install -r requirements.txt
```

project-root/
│
├── app/
│   ├── __init__.py                # Initialisiert das `app`-Modul
│   ├── main.py                    # Hauptanwendungsdatei für FastAPI-Routen
│   ├── models/                    # Enthält die Datenmodelle
│   │   ├── __init__.py
│   │   ├── product.py             # Modelle für Produkte
│   │   └── category.py            # Modelle für Kategorien
│   ├── schemas/                   # Enthält Pydantic-Schemas für Validierung
│   │   ├── __init__.py
│   │   ├── product.py             # Schemas für Produkte
│   │   └── category.py            # Schemas für Kategorien
│   ├── crud/                      # CRUD-Operationen
│   │   ├── __init__.py
│   │   ├── product.py             # CRUD-Operationen für Produkte
│   │   └── category.py            # CRUD-Operationen für Kategorien
│   ├── db/                        # Datenbankkonfigurationen und -operationen
│   │   ├── __init__.py
│   │   └── mongo.py               # MongoDB-Verbindungskonfiguration
│   ├── security/                  # Sicherheits- und Authentifizierungslogik
│   │   ├── __init__.py
│   │   ├── security.py            # Authentifizierungs- und Autorisierungslogik
│   │   └── roles.py               # Rollenbasierte Zugriffslogik
│   ├── routers/                   # Routers (Kapselung von Routen)
│   │   ├── __init__.py
│   │   ├── product_router.py      # Routen für Produkte
│   │   └── category_router.py     # Routen für Kategorien
│   └── utils/                     # Hilfsfunktionen
│       ├── __init__.py
│       └── helpers.py             # Hilfsfunktionen und Utility-Methoden
│
├── tests/                         # Tests
│   ├── __init__.py
│   ├── test_product.py            # Tests für Produkte
│   └── test_category.py           # Tests für Kategorien
│
├── requirements.txt               # Python-Abhängigkeiten
└── README.md                      # Projektbeschreibung und Anweisungen
