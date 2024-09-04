package com.gentlecorp.customer.util;

import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@Slf4j
@RequiredArgsConstructor
public class ControllerUtils {
  private final Validator validator;

  public <T> void validateDTO(T dto, Class<?>... groups) {
    // Perform validation
    final Collection<ConstraintViolation<T>> violations = validator.validate(dto, groups);

    // Handle violations
    if (!violations.isEmpty()) {
      log.debug("Validation failed: violations={}", violations);

      // Determine the type of DTO and throw the appropriate exception
      if (dto instanceof CustomerDTO) {
        @SuppressWarnings("unchecked")
        var customerViolations = (Collection<ConstraintViolation<CustomerDTO>>) (Collection<?>) violations;
        throw new ConstraintViolationsException(customerViolations, null);
      }

      if (dto instanceof ContactDTO) {
        @SuppressWarnings("unchecked")
        var contactViolations = (Collection<ConstraintViolation<ContactDTO>>) (Collection<?>) violations;
        throw new ConstraintViolationsException(null, contactViolations);
      }
//      else {
//        // Handle unexpected DTO types
//        throw new IllegalArgumentException(dto.getClass().getName());
//      }
    }
  }

  public String createETag(int version) {
    return String.format("\"%s\"", version);
  }

  public boolean isETagMatching(Optional<String> requestVersion, String currentVersion) {
    return Objects.equals(requestVersion.orElse(null), currentVersion);
  }

//  private <T> void validateDTO(T dto, Supplier<Collection<ConstraintViolation<T>>> validatorSupplier) {
//    final var violations = validatorSupplier.get();
//
//    if (!violations.isEmpty()) {
//      log.debug("create: violations={}", violations);
//
//      if (dto instanceof CustomerDTO) {
//        @SuppressWarnings("unchecked")
//        var customerViolations = (Collection<ConstraintViolation<CustomerDTO>>) (Collection<?>) violations;
//        throw new ConstraintViolationsException(customerViolations, null);
//      } else if (dto instanceof ContactDTO) {
//        @SuppressWarnings("unchecked")
//        var contactViolations = (Collection<ConstraintViolation<ContactDTO>>) (Collection<?>) violations;
//        throw new ConstraintViolationsException(null, contactViolations);
//      }
//    }
//  }
}
