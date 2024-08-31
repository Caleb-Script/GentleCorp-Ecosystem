package com.gentlecorp.transaction.exception;

import lombok.Getter;

/**
 * Exception thrown when access is denied due to missing roles.
 * <p>
 * This exception is used to indicate that a user or system component has attempted
 * to perform an action without having the required roles or permissions. It includes
 * details about the missing role that caused the access denial.
 * </p>
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @since 24.08.2024
 * @version 1.0
 */
@Getter
public class AccessForbiddenException extends RuntimeException {

  /**
   * The role that was required but not present.
   */
  private final String role;

  /**
   * Constructs a new {@code AccessForbiddenException} with a detailed message.
   * <p>
   * The message indicates that the role provided is insufficient for the requested
   * access, helping to pinpoint the specific role-related issue.
   * </p>
   *
   * @param role The role that was required but not found.
   */
  @SuppressWarnings("ParameterHidesMemberVariable")
  public AccessForbiddenException(final String role) {
    super("Unzureichende RoleType als : " + role);
    this.role = role;
  }
}
