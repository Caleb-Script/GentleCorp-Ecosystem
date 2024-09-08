package com.gentlecorp.invoice.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InsufficientFundsException extends RuntimeException {

  private final UUID accountId;

  public InsufficientFundsException(final UUID accountId) {
    super(String.format("Insufficient funds for account ID: %s ", accountId));
    this.accountId = accountId;
  }
}
