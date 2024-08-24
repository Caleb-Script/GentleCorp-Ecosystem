package com.gentle.bank.customer.service;

import com.gentle.bank.customer.MailProps;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.repository.CustomerRepository;
import com.gentle.bank.customer.exception.AccessForbiddenException;
import com.gentle.bank.customer.exception.EmailExistsException;
import com.gentle.bank.customer.exception.NotFoundException;
import com.gentle.bank.customer.exception.VersionOutdatedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Service class for managing write operations related to customer data.
 * <p>
 * This class provides methods for creating, updating, and deleting customer records.
 * It also includes functionality for handling customer emails and integrating with Keycloak for authentication.
 * </p>
 * <p>
 * All methods that modify customer data are transactional.
 * </p>
 * <img src="../../../../../asciidoc/CustomerWriteService.svg" alt="Class Diagram">
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteService {

  private final CustomerRepository customerRepository;
  private final MailService mailService;
  private final MailProps props;
  private final KeycloakService keycloakService;

  /**
   * Creates a new customer and saves it to the database.
   * <p>
   * This method first checks if the provided email already exists in the database.
   * If not, it saves the customer, sends a notification email, and registers the customer with Keycloak.
   * </p>
   *
   * @param customer The customer to be created.
   * @param password The password for the customer.
   * @param jwt      The JWT token of the currently authenticated user.
   * @return The created customer.
   * @throws EmailExistsException If a customer with the same email already exists.
   */
  @Transactional
  public Customer create(final Customer customer, final String password, final Jwt jwt) {
    log.debug("create: customer={}", customer);
    log.debug("create: address={}", customer.getAddress());

    if (customerRepository.existsByEmail(customer.getEmail()))
      throw new EmailExistsException(customer.getEmail());

    final var customerDb = customerRepository.save(customer);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    props.setTo(customerDb.getEmail());
    mailService.send(customerDb);

    final var role = customer.isElite()
      ? "gentlecorp-elite"
      : "gentlecorp-essential";

    keycloakService.signIn(customer, password, role);

    log.debug("create: customerDb={}", customerDb);
    return customerDb;
  }

  /**
   * Updates an existing customer in the database.
   * <p>
   * This method checks if the customer exists and whether the requesting user has permission to update the customer.
   * It also verifies the version of the customer record to ensure no concurrent modifications have occurred.
   * </p>
   *
   * @param customer The customer data to update.
   * @param id       The ID of the customer to update.
   * @param version  The version of the customer record.
   * @param role     The role of the requesting user.
   * @param username The username of the requesting user.
   * @param jwt      The JWT token of the currently authenticated user.
   * @return The updated customer.
   * @throws NotFoundException           If the customer with the given ID is not found.
   * @throws AccessForbiddenException    If the requesting user does not have permission to update the customer.
   * @throws VersionOutdatedException    If the provided version does not match the current version of the customer record.
   * @throws EmailExistsException        If the updated email already exists in the database.
   */
  @Transactional
  public Customer update(final Customer customer, final UUID id, final int version, final String role, final String username, final Jwt jwt) {
    log.debug("update: customer={}", customer);
    log.debug("update: id={}, version={}", id, version);
    log.trace("update: No constraints violated");

    final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

    if (!Objects.equals(customerDb.getUsername(), username) && !Objects.equals(role, "ADMIN")) {
      throw new AccessForbiddenException("YOU ARE NOT THE USER");
    }

    if (version != customerDb.getVersion()) {
      log.error("Version is not the current version");
      throw new VersionOutdatedException(version);
    }

    final var email = customer.getEmail();
    if (!Objects.equals(email, customerDb.getEmail()) && customerRepository.existsByEmail(email)) {
      log.error("update: email {} already exists", email);
      throw new EmailExistsException(email);
    }
    log.trace("update: No conflict with the email address");

    customerDb.set(customer);
    customerDb.setContactOptionsString(customer.getContactOptions());
    log.debug("NEW contactOptionsString: {}", customerDb.getContactOptionsString());

    final var updatedCustomerDb = customerRepository.save(customerDb);

    log.debug("update: updatedCustomerDB={}", customerDb);
    log.debug("update: updatedCustomer={}", customerDb);

    keycloakService.update(updatedCustomerDb, jwt);

    return updatedCustomerDb;
  }

  /**
   * Updates the password for a customer.
   * <p>
   * This method updates the password in Keycloak based on the provided JWT token.
   * </p>
   *
   * @param jwt      The JWT token of the currently authenticated user.
   * @param password The new password to set for the customer.
   */
  @Transactional
  public void updatePassword(final Jwt jwt, final String password) {
    log.debug("updatePassword: jwt={}", jwt);

    keycloakService.updatePassword(password, jwt);
  }

  /**
   * Deletes a customer by ID.
   * <p>
   * This method finds the customer by ID and deletes it from the database.
   * </p>
   *
   * @param id The ID of the customer to delete.
   * @throws NotFoundException If the customer with the given ID is not found.
   */
  @Transactional
  public void deleteById(final UUID id) {
    log.debug("deleteById: id={}", id);

    final var customer = customerRepository.findById(id).orElseThrow(NotFoundException::new);
    customerRepository.delete(customer);
  }
}
