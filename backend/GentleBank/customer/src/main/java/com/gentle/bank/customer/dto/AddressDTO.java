package com.gentle.bank.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link Address}
 */
public record AddressDTO(

  @Pattern(message = "Ungültiges Straßenformat. Es werden Buchstaben erwartet.", regexp = GERMAN_STREET_PATTERN)
  @NotNull(message = "Straße darf nicht null sein")
        String street,

  @NotNull(message = "Hausnummer darf nicht null sein")
        String houseNumber,

  @NotNull(message = "Die Postleitzahl darf nicht null sein")
  @Pattern(message = "Ungültiges Postleitzahlenformat. Es werden 5 Ziffern erwartet.", regexp = ZIP_CODE_PATTERN)
        String zipCode,

  @NotNull(message = "Das Bundesland darf nicht null sein")
        String state,

  @NotBlank(message = "Die Stadt darf nicht leer sein")
        String city
) {
  public static final String ZIP_CODE_PATTERN = "^\\d{5}$";
  public static final String GERMAN_STREET_PATTERN = "^[a-zA-ZäöüßÄÖÜ\\s]+(?:\\s\\d+)?$";
}
