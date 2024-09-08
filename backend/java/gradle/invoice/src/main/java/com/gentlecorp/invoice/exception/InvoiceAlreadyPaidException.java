package com.gentlecorp.invoice.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public final class InvoiceAlreadyPaidException extends RuntimeException {
  private final UUID invoiceId;

  public InvoiceAlreadyPaidException(final UUID invoiceId) {
    super(String.format("The invoice with ID %s has already been paid.", invoiceId));
    this.invoiceId = invoiceId;
  }
}
