import sys
from loguru import logger
from typing import Optional
from .settings import settings


def custom_logger(class_name: str, color: Optional[str] = None, time: Optional[bool] = True):
    logger.remove()  # Entfernt alle bestehenden Konfigurationen
    level = settings.LOG_LEVEL

    # Definiere das Format basierend auf den Parametern
    time_format = "{time:MMMM D, YYYY - HH:MM:SS}" if time else "{time:YYYY-MM-DD}"
    color_format = f"{color}" if color else "level"

    # Füge Konsole Logger hinzu
    logger.add(
        sys.stdout,
        level=level,
        format=f"<{color_format}>{time_format}</{color_format}> | <{color_format}>{{level}}</{color_format}> | <{color_format}>{{message}}</{color_format}> | <{color_format}>{{extra}}</{color_format}>",
    )

    logger.bind(className=class_name)

    # Füge Datei Logger hinzu
    logger.add(
        "logs/file_{time: MMMM D, YYYY}.log",
        # rotation="1 MB",
        rotation="5 seconds",
        retention="10 seconds",
        level=level,
        format=f"{time_format} {{level}} --- {{message}}",
        serialize=True,
    )
    child = logger.bind(file=class_name)
    return child


