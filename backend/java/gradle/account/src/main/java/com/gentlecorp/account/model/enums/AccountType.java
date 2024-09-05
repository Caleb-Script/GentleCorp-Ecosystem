package com.gentlecorp.account.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum AccountType {
    SAVINGS("S"),
    CHECKING("CH"),
    CREDIT("CR"),
    DEPOSIT("D"),
    INVESTMENT("I"),
    LOAN("L");

    private final String type;

    @JsonCreator
    public static AccountType of(final String value) {
        return Stream.of(values())
                .filter(accountType -> accountType.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
