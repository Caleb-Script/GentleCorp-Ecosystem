package com.gentlecorp.transaction.exception;

import lombok.Getter;

/**
 * Exception thrown when an attempt is made to create a user with a customerUsername that already exists.
 * <p>
 * This exception is used to indicate that the customerUsername provided during user registration
 * or update is already taken by another user. This prevents duplicate usernames in the system.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class UsernameExistsException extends RuntimeException {
  /**
   * The customerUsername that already exists.
   */
  private final String username;

  /**
   * Constructs a new {@code UsernameExistsException} with the specified customerUsername.
   *
   * @param username The customerUsername that already exists.
   */
  public UsernameExistsException(final String username) {
    super("Der Benutzername " + username + " existiert bereits.");
    this.username = username;
  }
}
