package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Enum representing various contact options for customers.
 * <p>
 * This enum defines different types of contact methods that a customer might prefer. It is used to categorize
 * contact options such as email, phone, mail, and SMS.
 * </p>
 *
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
@RequiredArgsConstructor
public enum ContactOptionsType {
  /**
   * Contact option via email.
   */
  EMAIL("Email"),

  /**
   * Contact option via phone.
   */
  PHONE("Phone"),

  /**
   * Contact option via traditional mail.
   */
  MAIL("Mail"),

  /**
   * Contact option via SMS.
   */
  SMS("SMS");

  private final String type;

  /**
   * Gets the string representation of the contact option.
   *
   * @return the type of contact option as a string.
   */
  @JsonValue
  public String getType() {
    return type;
  }

  /**
   * Creates a {@link ContactOptionsType} from a string value.
   * <p>
   * This method is used to convert a string representation of a contact option into its corresponding enum constant.
   * </p>
   *
   * @param value the string value of the contact option.
   * @return the {@link ContactOptionsType} enum constant corresponding to the provided string value,
   *         or {@code null} if no matching constant is found.
   */
  @JsonCreator
  public static ContactOptionsType of(final String value) {
    return Stream.of(values())
      .filter(option -> option.type.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
