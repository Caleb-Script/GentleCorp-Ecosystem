package com.gentle.bank.customer.security;

import lombok.Getter;

/**
 * Exception, falls ein neues Passwort ungültig ist.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class PasswordInvalidException extends RuntimeException {
    private final String password;

    PasswordInvalidException(final String password) {
        super("Ungueltiges Passwort " + password);
        this.password = password;
    }
}
