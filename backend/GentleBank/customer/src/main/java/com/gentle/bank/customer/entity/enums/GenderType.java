package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Enumeration for gender types. This can be used, for example, to implement radio buttons on the client side.
 * <p>
 * Represents various gender types such as male, female, or diverse.
 * </p>
 * <p>
 * Each enum value is associated with a string representation used for serialization and deserialization.
 * The {@link JsonCreator} annotation is used for deserialization, and the {@link JsonValue} annotation is used for serialization.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * GenderType gender = GenderType.of("M");
 * String genderString = gender.getType(); // Returns "M"
 * }</pre>
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
@RequiredArgsConstructor
public enum GenderType {

  /**
   * Male gender type.
   */
  MALE("M"),

  /**
   * Female gender type.
   */
  FEMALE("F"),

  /**
   * Diverse gender type.
   */
  DIVERSE("D");

  /** The string representation of the gender type. */
  private final String type;

  /**
   * Returns the {@link GenderType} associated with the given string value.
   * <p>
   * This method is used to convert a string representation of a gender type into its corresponding enum constant.
   * For example, it can convert the string "M" to the MALE enum constant.
   * </p>
   *
   * @param value The string value representing the gender type.
   * @return The {@link GenderType} enum constant associated with the given string value, or {@code null} if no match is found.
   */
  @JsonCreator
  public static GenderType of(final String value) {
    return Stream.of(values())
      .filter(gender -> gender.type.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }

  /**
   * Returns the string representation of the gender type.
   * <p>
   * This method returns the string value associated with the gender type. For example, it returns "M" for the MALE enum constant.
   * </p>
   *
   * @return The string representation of the gender type.
   */
  @JsonValue
  public String getType() {
    return type;
  }
}
