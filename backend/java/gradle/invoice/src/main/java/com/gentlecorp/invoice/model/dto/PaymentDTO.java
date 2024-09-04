package com.gentlecorp.invoice.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDTO(
  BigDecimal amount
) {
  public interface OnCreate { }
}
