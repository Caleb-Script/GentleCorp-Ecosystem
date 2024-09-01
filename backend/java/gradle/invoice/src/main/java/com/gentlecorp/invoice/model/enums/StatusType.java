package com.gentlecorp.invoice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum StatusType {
  PENDING("PENDING"),
  PAID("PAID"),
  OVERDUE("OVERDUE");

    private final String type;

    @JsonCreator
    public static StatusType of(final String value) {
        return Stream.of(values())
                .filter(statusType -> statusType.type.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
