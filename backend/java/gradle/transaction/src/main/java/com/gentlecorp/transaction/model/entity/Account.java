package com.gentlecorp.transaction.model.entity;

import java.math.BigDecimal;

public record Account(
  String customerUsername,
  BigDecimal balance
) {
}
