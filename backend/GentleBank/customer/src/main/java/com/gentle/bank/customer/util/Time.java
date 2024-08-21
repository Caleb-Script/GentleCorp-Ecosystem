package com.gentle.bank.customer.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Time {
    public static LocalDateTime localTime(LocalDateTime localDateTime) {
        // Aktuelles Datum und Uhrzeit mit Mikrosekunden


        // Benutzerdefinierter Formatter ohne Mikrosekunden
        DateTimeFormatter formatterWithoutMicros = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Datum und Uhrzeit formatieren und dann parsen, um die Mikrosekunden zu entfernen
        return LocalDateTime.parse(localDateTime.format(formatterWithoutMicros), formatterWithoutMicros);
    }
}
