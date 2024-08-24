package com.gentle.bank.customer.dto;

/**
 * Data Transfer Object (DTO) for login credentials.
 * <p>
 * This DTO represents the JSON structure used for logging in with a username and password.
 * It is used to encapsulate the credentials provided by a user during the login process.
 * </p>
 *
 * @param username The username of the user attempting to log in. Must not be {@code null} or empty.
 * @param password The password of the user attempting to log in. Must not be {@code null} or empty.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record LoginDTO(
  /**
   * The username of the user attempting to log in.
   * <p>
   * This field must not be {@code null} or empty.
   * </p>
   */
  String username,

  /**
   * The password of the user attempting to log in.
   * <p>
   * This field must not be {@code null} or empty.
   * </p>
   */
  String password
) {
}
