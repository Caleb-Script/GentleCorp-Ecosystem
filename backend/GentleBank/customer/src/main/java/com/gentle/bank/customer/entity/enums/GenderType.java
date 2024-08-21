package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Aufzählung für Geschlecht. Dies kann beispielsweise verwendet werden, um Radio-Buttons auf der Clientseite zu implementieren.
 *
 * <p>Repräsentiert verschiedene Geschlechtertypen wie männlich, weiblich oder divers.</p>
 *
 * <p>Jeder Enum-Wert ist mit einer Zeichenfolgenrepräsentation verbunden, die für Serialisierung und Deserialisierung verwendet wird.
 * Die {@link JsonCreator}-Annotation wird für die Deserialisierung verwendet, und die {@link JsonValue}-Annotation wird für die Serialisierung verwendet.</p>
 *
 * <p>Beispielverwendung:
 * <pre>{@code
 * GenderType gender = GenderType.of("M");
 * String genderString = gender.getType(); // Gibt "M" zurück
 * }</pre>
 * </p>
 */
@Getter
@RequiredArgsConstructor
public enum GenderType {
    /** Männliches Geschlecht. */
    MALE("M"),

    /** Weibliches Geschlecht. */
    FEMALE("F"),

    /** Diverser Geschlechtstyp. */
    DIVERSE("D");

    /** Die Zeichenfolgen repräsentation des Geschlechtstyps. */
    private final String type;

    /**
     * Gibt den Geschlechtstyp zurück, der mit dem angegebenen Zeichenfolgen wert verbunden ist.
     *
     * @param value Der Zeichenfolgen wert, der den Geschlechtstyp darstellt.
     * @return Der Enum-Wert GenderType, der mit dem angegebenen Zeichenfolgen wert verbunden ist, oder null, wenn keine Übereinstimmung gefunden wird.
     */
    @JsonCreator
    public static GenderType of(final String value) {
        return Stream.of(values())
                .filter(gender -> gender.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * Gibt die Zeichenfolgen repräsentation des Geschlechtstyps zurück.
     *
     * @return Die Zeichenfolgen repräsentation des Geschlechtstyps.
     */
    @JsonValue
    public String getType() {
        return type;
    }
}
