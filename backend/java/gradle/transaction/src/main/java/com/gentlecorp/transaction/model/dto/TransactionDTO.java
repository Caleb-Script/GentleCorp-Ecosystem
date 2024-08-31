package com.gentlecorp.transaction.model.dto;

import com.gentlecorp.transaction.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(
  BigDecimal amount,
  UUID sender,
  UUID receiver
) {
  public interface OnCreate { }
}
