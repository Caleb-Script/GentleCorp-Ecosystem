package com.gentlecorp.account.exception;

import lombok.Getter;

/**
 * Exception thrown when an email address already exists in the system.
 * <p>
 * This exception is used to indicate that an attempt was made to create or update a customer record
 * with an email address that is already in use by another customer. The exception provides details
 * about the conflicting email address.
 * </p>
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @since 24.08.2024
 * @version 1.0
 */
@Getter
public class EmailExistsException extends RuntimeException {
  private final String email;

  public EmailExistsException(final String email) {
    super(String.format("Die Emailadresse %s existiert bereits", email));
    this.email = email;
  }
}
