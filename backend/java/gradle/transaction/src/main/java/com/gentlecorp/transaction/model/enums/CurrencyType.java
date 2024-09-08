package com.gentlecorp.transaction.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum CurrencyType {

  EUR("EURO"),
  USD("US-Dollar"),
  GBP("Pounds"),
  GHS("Ghana Cidis");

  private final String currency;

  @JsonCreator
  public static CurrencyType of(final String value) {
    return Stream.of(values())
      .filter(accountType -> accountType.currency.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
