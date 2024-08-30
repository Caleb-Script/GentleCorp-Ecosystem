package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<CustomerDTO>> violationsDTO;
  public ConstraintViolationsException(
    final Collection<ConstraintViolation<CustomerDTO>> violations
  ) {
    super("Constraints sind verletzt");
    this.violationsDTO = violations;
  }
}
