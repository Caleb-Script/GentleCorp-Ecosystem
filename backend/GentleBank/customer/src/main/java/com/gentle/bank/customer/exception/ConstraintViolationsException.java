package com.gentle.bank.customer.exception;

import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.entity.Customer;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
  private final transient Collection<ConstraintViolation<CustomerDTO>> violationsDTO;

    public ConstraintViolationsException(
      final Collection<ConstraintViolation<CustomerDTO>> violations
    ) {
        super("Constraints sind verletzt");
        this.violationsDTO = violations;
    }
}
