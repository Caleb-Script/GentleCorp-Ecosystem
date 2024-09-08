package com.gentlecorp.invoice.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(
  BigDecimal amount,
  String currency,
  UUID sender,
  UUID receiver
) {
}
