package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum MaritalStatusType {
  SINGLE("S"),
  MARRIED("M"),
  DIVORCED("D"),
  WIDOWED("W"),
  OTHER("O");

  private final String status;

  @JsonCreator
  public static MaritalStatusType of(final String value) {
    return Stream.of(values())
      .filter(statusType -> statusType.status.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
