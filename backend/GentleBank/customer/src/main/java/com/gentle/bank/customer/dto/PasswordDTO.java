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
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
public record PasswordDTO(
  /**
   * The password value.
   */
  String password
) {
}
