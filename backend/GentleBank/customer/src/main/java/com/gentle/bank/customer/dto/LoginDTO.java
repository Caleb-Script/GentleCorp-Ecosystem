package com.gentle.bank.customer.dto;

/**
 * Data Transfer Object (DTO) for login credentials.
 * <p>
 * This DTO represents the JSON structure used for logging in with a username and password.
 * </p>
 *
 * @param username The username of the user attempting to log in.
 * @param password The password of the user attempting to log in.
 *
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
public record LoginDTO(
  /**
   * The username of the user attempting to log in.
   */
  String username,

  /**
   * The password of the user attempting to log in.
   */
  String password
) {
}
