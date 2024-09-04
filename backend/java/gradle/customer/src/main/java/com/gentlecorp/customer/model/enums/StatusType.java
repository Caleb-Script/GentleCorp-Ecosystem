package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum StatusType {
    ACTIVE("A"),
    BLOCKED("B"),
  INACTIVE("I"),
    CLOSED("C");

    private final String status;

    @JsonCreator
    public static StatusType of(final String value) {
        return Stream.of(values())
                .filter(contactOptions -> contactOptions.status.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("Invalid EmploymentStatus: " + value));
    }
}
