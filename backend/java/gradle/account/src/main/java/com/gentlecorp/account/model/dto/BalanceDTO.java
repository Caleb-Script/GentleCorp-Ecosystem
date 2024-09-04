package com.gentlecorp.account.model.dto;

import java.math.BigDecimal;

public record BalanceDTO(
  BigDecimal amount
) {
}
