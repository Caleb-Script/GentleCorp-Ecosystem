package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

/**
 * Enum representing scopes as defined by <a href="https://www.rfc-editor.org/rfc/rfc6749.html">OAuth 2.0</a>.
 * <p>
 * This enum defines various scopes that can be requested in an OAuth 2.0 authentication process. Currently, only
 * the "email profile" scope is supported.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public enum ScopeType {
  /**
   * Scope "email profile" for OAuth 2.0.
   */
  EMAIL_PROFILE("email profile");

  /** The string representation of the scope. */
  private final String value;

  /**
   * Constructor to initialize the enum with the given string value.
   *
   * @param value The string representation of the scope.
   */
  ScopeType(final String value) {
    this.value = value;
  }

  /**
   * Returns the string representation of the scope.
   * <p>
   * This method is used by Jackson to serialize the enum value into a JSON format.
   * </p>
   *
   * @return The internal string value of the scope.
   */
  @JsonValue
  public String getValue() {
    return value;
  }

  /**
   * Converts a string value to its corresponding {@link ScopeType} enum constant.
   * <p>
   * This method is used by Jackson to deserialize a JSON string into an enum value.
   * </p>
   *
   * @param value The string value representing the scope.
   * @return The {@link ScopeType} enum constant corresponding to the provided string value, or {@code null} if no match is found.
   */
  @JsonCreator
  public static ScopeType of(final String value) {
    return Stream.of(values())
      .filter(scope -> scope.value.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
