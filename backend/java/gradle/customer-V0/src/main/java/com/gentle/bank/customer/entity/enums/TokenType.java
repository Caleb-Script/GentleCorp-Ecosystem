package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

/**
 * Enum representing token types for OAuth 2.0.
 * <p>
 * This enum defines various token types that can be used in OAuth 2.0 authentication processes. Currently, only the
 * "Bearer" token type is supported.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public enum TokenType {
  /**
   * Bearer token type.
   */
  BEARER("Bearer");

  /** The string representation of the token type. */
  private final String value;

  /**
   * Constructor to initialize the enum with the given string value.
   *
   * @param value The string representation of the token type.
   */
  TokenType(final String value) {
    this.value = value;
  }

  /**
   * Returns the string representation of the token type.
   * <p>
   * This method is used by Jackson to serialize the enum value into a JSON format.
   * </p>
   *
   * @return The internal string value of the token type.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Converts a string value to its corresponding {@link TokenType} enum constant.
   * <p>
   * This method is used by Jackson to deserialize a JSON string into an enum value.
   * </p>
   *
   * @param value The string value representing the token type.
   * @return The {@link TokenType} enum constant corresponding to the provided string value, or {@code null} if no match is found.
   */
  @JsonCreator
  public static TokenType of(final String value) {
    return Stream.of(values())
      .filter(token -> token.value.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
