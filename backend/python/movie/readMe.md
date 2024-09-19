# FastAPI Project

## Einrichtung und Ausführung

1. **Virtuelle Umgebung erstellen**

    ```bash
    python3 -m venv .venv_movie
    ```

2. **Virtuelle Umgebung aktivieren**

   Auf Unix-ähnlichen Systemen (Linux, macOS):

    ```bash
    source .venv_movie/bin/activate
    ```

   Auf Windows:

    ```bash
    .venv_movie\bin\Activate.ps1   
    ```

3. **Abhängigkeiten installieren**

    ```bash
    pip3 install -r requirements.txt
    ```

4. **Projekt starten**

    ```bash
    uvicorn app.main:app --reload
    ```


## TODO

- 