package com.gentlecorp.transaction.exception;

import com.gentlecorp.transaction.model.dto.TransactionDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<TransactionDTO>> violationsDTO;
  public ConstraintViolationsException(
    final Collection<ConstraintViolation<TransactionDTO>> violations
  ) {
    super("Constraints sind verletzt");
    this.violationsDTO = violations;
  }
}
