package com.gentlecorp.account.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceDTO2(
  UUID id,
  BigDecimal amount
) {
}
