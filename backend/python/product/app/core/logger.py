import sys
from loguru import logger
from typing import Optional
from .settings import settings


def custom_logger(class_name: str, color: Optional[str] = None, time: Optional[bool] = True):
    logger.remove()  # Entfernt alle bestehenden Konfigurationen
    level = settings.LOG_LEVEL

    # Definiere das Zeitformat basierend auf den Parametern
    time_format = "{time:MMMM D, YYYY - HH:MM:SS}" if time else "{time:YYYY-MM-DD}"

    # Definiere ANSI-Farbcodes
    colors = {
        "INFO": "\033[35m",  # Pink
        "SUCCESS": "\033[32m",  # Grün
        "WARNING": "\033[33m",  # Gelb
        "ERROR": "\033[31m",  # Rot
        "CRITICAL": "\033[1m\033[31m",  # Fett Rot
        "DEBUG": "\033[34m",  # Blau
    }
    reset_color = "\033[0m"

    # Standardformat für Log-Nachrichten mit ANSI-Farbcodes
    def format_record(record):
        color = colors.get(record["level"].name, "")
        extra_str = " | ".join(f"{k}={v}" for k, v in record["extra"].items())
        return (
            f"{color}{time_format.format(**record)}{reset_color} | "
            f"{color}{{level}}{reset_color} | "
            f"{color}{{message}}{reset_color} | "
            f"{color}{extra_str}{reset_color}\n"
        ).format(**record)

    # Füge Konsole Logger hinzu
    logger.add(
        sys.stdout,
        level="DEBUG",  # Setze auf DEBUG, um alle Levels einschließlich SUCCESS zu erfassen
        format=format_record,
    )

    # Füge Datei Logger hinzu
    logger.add(
        "logs/file_{time:MMMM_D_YYYY}.log",
        rotation="5 seconds",  # Rotation nach 5 Sekunden für Demo-Zwecke
        retention="10 seconds",
        level=level,
        format=f"{time_format} {{level}} --- {{message}}",
        serialize=True,
    )

    # Binde zusätzliche Informationen wie den Klassennamen
    child_logger = logger.bind(file=class_name)
    return child_logger
