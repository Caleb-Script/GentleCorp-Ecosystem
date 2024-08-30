package com.gentlecorp.account.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account"),
    CREDIT("Credit Account"),
    DEPOSIT("Deposit Account"),
    INVESTMENT("Investment Account"),
    LOAN("Loan Account");

    private final String type;

    @JsonCreator
    public static AccountType of(final String value) {
        return Stream.of(values())
                .filter(accountType -> accountType.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
