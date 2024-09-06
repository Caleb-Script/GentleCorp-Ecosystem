package com.gentlecorp.transaction.exception;

public class InsufficientFundsException extends RuntimeException {

  public InsufficientFundsException() {
    super("Du hast nicht genügend Geld");
  }
}
