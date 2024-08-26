package com.gentle.bank.customer.exception;

import com.gentle.bank.customer.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.Collection;

/**
 * Exception thrown when validation constraints on a {@link CustomerDTO} are violated.
 * <p>
 * This exception is used to indicate that one or more constraints defined on the {@link CustomerDTO}
 * object have been violated. It contains details about the specific constraint violations that occurred.
 * </p>
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @since 24.08.2024
 * @version 1.0
 */
@Getter
public class ConstraintViolationsException extends RuntimeException {

  /**
   * The collection of constraint violations on the {@link CustomerDTO} object.
   * <p>
   * This field contains the specific constraint violations that triggered the exception,
   * providing details about what constraints were violated and the invalid values.
   * </p>
   */
  private final transient Collection<ConstraintViolation<CustomerDTO>> violationsDTO;

  /**
   * Constructs a new {@code ConstraintViolationsException} with a detailed message and a collection of violations.
   * <p>
   * The message indicates that one or more constraints have been violated, and the collection provides
   * detailed information about each violation.
   * </p>
   *
   * @param violations A collection of constraint violations on the {@link CustomerDTO} object.
   */
  public ConstraintViolationsException(
    final Collection<ConstraintViolation<CustomerDTO>> violations
  ) {
    super("Constraints sind verletzt");
    this.violationsDTO = violations;
  }
}
