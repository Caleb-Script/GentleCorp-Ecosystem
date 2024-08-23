package com.gentle.bank.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Javadoc-Kommentar für die UnauthorizedException-Klasse.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SignUpException extends RuntimeException {

    /**
     * Konstruktor für UnauthorizedException.
     *
     * @param message Fehlermeldung der Ausnahme.
     */
    public SignUpException(final String message) {
        super(message);
    }
}
