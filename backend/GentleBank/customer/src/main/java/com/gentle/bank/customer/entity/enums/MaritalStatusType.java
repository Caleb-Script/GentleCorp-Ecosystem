package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Enum representing various marital statuses. This can be used, for example, to implement a dropdown menu on the client side.
 *
 * <p>Represents different marital status types such as single, married, divorced, widowed, or other.</p>
 *
 * <p>Each enum value is associated with a string representation used for serialization and deserialization.
 * The {@link JsonCreator} annotation is used for deserialization, and the {@link JsonValue} annotation is used for serialization.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * MaritalStatusType status = MaritalStatusType.of("M");
 * String statusString = status.getType(); // Returns "M"
 * }</pre>
 * </p>
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @since 23.08.2024
 * @version 1.0
 */
@RequiredArgsConstructor
public enum MaritalStatusType {
  /** Single marital status. */
  SINGLE("S"),

  /** Married marital status. */
  MARRIED("M"),

  /** Divorced marital status. */
  DIVORCED("D"),

  /** Widowed marital status. */
  WIDOWED("W"),

  /** Other marital status. */
  OTHER("O");

  /** The string representation of the marital status type. */
  private final String type;

  /**
   * Returns the string representation of the marital status type.
   *
   * @return The string representation of the marital status type.
   */
  @JsonValue
  public String getType() {
    return type;
  }

  /**
   * Returns the {@link MaritalStatusType} associated with the given string value.
   *
   * @param value The string value representing the marital status type.
   * @return The {@link MaritalStatusType} enum constant associated with the given string value, or {@code null} if no match is found.
   */
  @JsonCreator
  public static MaritalStatusType of(final String value) {
    return Stream.of(values())
      .filter(status -> status.type.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
