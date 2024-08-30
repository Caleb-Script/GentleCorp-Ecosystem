package com.gentlecorp.customer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddressDTO(
  @Pattern(message = "Invalid street format. Letters are expected.", regexp = STREET_PATTERN)
  @NotNull(message = "Street must not be null")
  String street,

  @NotNull(message = "House number must not be null")
  String houseNumber,

  @NotNull(message = "Zip code must not be null")
  String zipCode,

  @NotNull(message = "City must not be null")
  String city,

  @NotNull(message = "State must not be null")
  String state,

  @NotBlank(message = "Country must not be empty")
  String country
) {
  public static final String STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
}
