package com.gentle.bank.customer.service.patch;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PatchOperationType {

    REPLACE("replace"),

    ADD("add"),

    REMOVE("remove");

    private final String value;

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
