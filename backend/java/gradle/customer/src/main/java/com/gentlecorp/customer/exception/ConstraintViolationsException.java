package com.gentlecorp.customer.exception;

import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class ConstraintViolationsException extends RuntimeException {
  private final transient Collection<ConstraintViolation<CustomerDTO>> violationsDTO;

  public ConstraintViolationsException(
    final Collection<ConstraintViolation<CustomerDTO>> violations
  ) {
    super(formatMessage(violations));
    this.violationsDTO = violations;
  }

  private static String formatMessage(Collection<ConstraintViolation<CustomerDTO>> violations) {
    return violations.stream()
      .map(violation -> String.format(
        "'%s': %s",
        violation.getPropertyPath(),
        violation.getMessage()
      ))
      .collect(Collectors.joining(" | ", "Constraints violated: ", ""));
  }
}
