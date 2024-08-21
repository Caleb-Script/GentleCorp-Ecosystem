package com.gentle.bank.customer.service.exception;

import com.gentle.bank.customer.entity.Customer;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

@Getter
public class ConstraintViolationsException extends RuntimeException {
    /**
     * Die verletzten Constraints.
     */
    private final transient Collection<ConstraintViolation<Customer>> violations;

    public ConstraintViolationsException(
            final Collection<ConstraintViolation<Customer>> violations
    ) {
        super("Constraints sind verletzt");
        this.violations = violations;
    }
}
