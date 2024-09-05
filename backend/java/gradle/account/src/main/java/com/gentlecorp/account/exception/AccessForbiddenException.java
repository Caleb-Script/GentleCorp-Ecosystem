package com.gentlecorp.account.exception;

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

  private final String role;
  private final String username;

  @SuppressWarnings("ParameterHidesMemberVariable")
  public AccessForbiddenException(final String role) {
    super("Unzureichende RoleType als : " + role);
    this.role = role;
    this.username = null;
  }

  public AccessForbiddenException(final String username, final String role) {
    super(String.format("nur für den benutzer %s", username));
    this.role = role;
    this.username = username;
  }
}
