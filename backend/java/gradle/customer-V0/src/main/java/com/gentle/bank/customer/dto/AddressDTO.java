package com.gentle.bank.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) for representing an address.
 * <p>
 * This DTO encapsulates the details of an address, including street, house number, postal code,
 * state, city, and country. It is used for transferring address information between different
 * layers of the application, particularly in requests and responses involving address data.
 * </p>
 *
 * <p>
 * Validation constraints are applied to ensure that address fields are properly formatted and not null where required.
 * The {@link #STREET_PATTERN} regular expression is used to validate the format of the street field.
 * </p>
 *
 * @param street The street of the address. Must match the {@link #STREET_PATTERN} regular expression.
 * @param houseNumber The house number of the address. Must not be {@code null}.
 * @param zipCode The postal code of the address. Must not be {@code null}.
 * @param state The state of the address. Must not be {@code null}.
 * @param city The city of the address. Must not be blank.
 * @param country The country of the address. Must not be blank.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record AddressDTO(
  /**
   * The street of the address. This field must not be {@code null} and must match the {@link #STREET_PATTERN} regex.
   * <p>
   * The pattern requires the street to consist of letters and spaces, optionally followed by a space and digits (for street numbers).
   * </p>
   */
  @Pattern(message = "Invalid street format. Letters are expected.", regexp = STREET_PATTERN)
  @NotNull(message = "Street must not be null")
  String street,

  /**
   * The house number of the address. This field must not be {@code null}.
   */
  @NotNull(message = "House number must not be null")
  String houseNumber,

  /**
   * The postal code of the address. This field must not be {@code null}.
   */
  @NotNull(message = "Zip code must not be null")
  String zipCode,

  /**
   * The state of the address. This field must not be {@code null}.
   */
  @NotNull(message = "State must not be null")
  String state,

  /**
   * The city of the address. This field must not be blank.
   */
  @NotBlank(message = "City must not be empty")
  String city,

  /**
   * The country of the address. This field must not be blank.
   */
  @NotBlank(message = "Country must not be empty")
  String country
) {
  /**
   * Regular expression pattern for validating the street field. It requires the street to consist of letters and spaces,
   * optionally followed by a space and digits (for street numbers).
   */
  public static final String STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
}
