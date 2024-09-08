package com.gentlecorp.transaction.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Getter
public class InvalidTransactionException extends RuntimeException {
  private final UUID id;

  public InvalidTransactionException(final UUID id) {

    super(String.format("Invalid transaction AccountId: %s cant be the sender and the receiver", id));
    this.id = id;
  }
}
