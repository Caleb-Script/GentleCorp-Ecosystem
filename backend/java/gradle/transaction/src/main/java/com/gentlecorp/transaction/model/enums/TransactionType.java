package com.gentlecorp.transaction.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("Deposit"),
  WITHDRAWAL("Withdrawal"),
  TRANSFER("Transfer"),
    INCOME("Income"),
  REFUND("Refund"),
  PAYMENT("Payment");

    private final String type;

    @JsonCreator
    public static TransactionType of(final String value) {
        return Stream.of(values())
                .filter(transactionType -> transactionType.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
