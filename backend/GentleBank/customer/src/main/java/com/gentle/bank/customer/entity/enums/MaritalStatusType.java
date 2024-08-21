package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Enum for Marital Status. This can be used, for example, to implement a dropdown menu on the client side.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@RequiredArgsConstructor
public enum MaritalStatusType {
    SINGLE("S"),

    MARRIED("M"),

    DIVORCED("D"),

    WIDOWED("W"),
    OTHER("O");

    private final String type;

    @JsonValue
    public String getType() {
        return type;
    }

    @JsonCreator
    public static MaritalStatusType of(final String value) {
        return Stream.of(values())
                .filter(status -> status.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
