package com.gentlecorp.customer.util;

import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.exception.ContactExistsException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class Validation {
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

  public static void validateContact(Contact contact, Customer customerDb) {
    customerDb.getContacts().forEach(
      contactDb -> {
        if (contactDb.getLastName().equals(contact.getLastName()) && contactDb.getFirstName().equals(contact.getFirstName())) {
          throw new ContactExistsException(contact.getLastName(), contact.getFirstName());
        }
      });
  }

  public static void validateContact(Contact contact, Customer customerDb, final UUID contactId) {
    final var existingContact = customerDb.getContacts()
      .stream()
      .filter(contactDb -> contactDb.getLastName().equals(contact.getLastName()) && contactDb.getFirstName().equals(contact.getFirstName()))
      .findFirst()
      .orElse(null);

    if (existingContact == null) {
      return;
    }

    if (existingContact.getId().equals(contactId)) {
      log.error("Contact with id {} already exists", contactId);
      return;
    }

    if (existingContact.getFirstName().equals(contact.getFirstName()) && existingContact.getLastName().equals(contact.getLastName())) {
      log.error("Contact with name {} already exists", contact.getFirstName());
      throw new ContactExistsException(contact.getLastName(), contact.getFirstName());
    }
  }
}
