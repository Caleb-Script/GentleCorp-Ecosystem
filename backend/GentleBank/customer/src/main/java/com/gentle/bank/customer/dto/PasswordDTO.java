package com.gentle.bank.customer.dto;

/**
 * Data Transfer Object (DTO) for representing a password.
 * <p>
 * This DTO is used to encapsulate a password value, typically for purposes such as
 * password updates or resets.
 * </p>
 *
 * @param password The password value.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record PasswordDTO(
  /**
   * The password value.
   * <p>
   * This field must contain the password as a plain string.
   * </p>
   */
  String password
) {
}
