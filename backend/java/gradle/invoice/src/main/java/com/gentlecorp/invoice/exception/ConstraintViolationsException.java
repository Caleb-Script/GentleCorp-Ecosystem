package com.gentlecorp.invoice.exception;

import com.gentlecorp.invoice.model.dto.InvoiceDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<InvoiceDTO>> violationsDTO;
  public ConstraintViolationsException(
    final Collection<ConstraintViolation<InvoiceDTO>> violations
  ) {
    super("Constraints sind verletzt");
    this.violationsDTO = violations;
  }
}
