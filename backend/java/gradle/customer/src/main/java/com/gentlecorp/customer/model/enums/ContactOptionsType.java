package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum ContactOptionsType {
  EMAIL("Email"),
  PHONE("Phone"),
  LETTER("Letter"),
  SMS("SMS");

  private final String option;

  @JsonCreator
  public static ContactOptionsType of(final String value) {
    return Stream.of(values())
      .filter(optionsType -> optionsType.option.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
