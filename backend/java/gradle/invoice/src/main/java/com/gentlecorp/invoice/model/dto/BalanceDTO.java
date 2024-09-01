package com.gentlecorp.invoice.model.dto;

import java.math.BigDecimal;

public record BalanceDTO(
  BigDecimal newValue
) {
}
