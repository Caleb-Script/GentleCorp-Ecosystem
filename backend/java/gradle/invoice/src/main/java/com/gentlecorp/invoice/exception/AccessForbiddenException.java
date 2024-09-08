package com.gentlecorp.invoice.exception;

import lombok.Getter;

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

  public AccessForbiddenException(final String message, final int role) {
    super(message);
    this.role = null;
    this.username = null;
  }
}
