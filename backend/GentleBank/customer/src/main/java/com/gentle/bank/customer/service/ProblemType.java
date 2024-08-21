package com.gentle.bank.customer.service;

import lombok.Getter;

/**
 * Enum für ProblemDetail.type.
 *
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public enum ProblemType {
    /**
     * Constraints als Fehlerursache.
     */
    CONSTRAINTS("constraints"),

    /**
     * Fehler, wenn z.B. Emailadresse bereits existiert.
     */
    UNPROCESSABLE("unprocessable"),

    /**
     * Fehler beim Header If-Match.
     */
    PRECONDITION("precondition"),

    /**
     * Fehler bei z.B. einer Patch-Operation.
     */
    BAD_REQUEST("badRequest"),
    FORBIDDEN("forbidden"),
    /**
     * Fehler, wenn z.B. eine ID nicht existiert
     */
    NOT_FOUND("notFound");

    private final String value;

    ProblemType(final String value) {
        this.value = value;
    }
}
