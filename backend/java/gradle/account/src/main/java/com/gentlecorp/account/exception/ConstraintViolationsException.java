package com.gentlecorp.account.exception;

import com.gentlecorp.account.model.dto.AccountDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<AccountDTO>> violationsDTO;
  public ConstraintViolationsException(
    final Collection<ConstraintViolation<AccountDTO>> violations
  ) {
    super("Constraints sind verletzt");
    this.violationsDTO = violations;
  }
}
