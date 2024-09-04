package com.gentlecorp.invoice.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceDTO(
  BigDecimal amount,
  LocalDate dueDate) {
  public interface OnCreate { }
}
