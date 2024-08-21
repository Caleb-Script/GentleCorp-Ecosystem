package com.gentle.bank.customer.service.exception;

import lombok.Getter;

@Getter
public class EmailExistsException extends RuntimeException {
    private final String email;

    public EmailExistsException(final String email) {
        super(STR."Die Emailadresse \{email} existiert bereits");
        this.email = email;
    }
}
