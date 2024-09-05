package com.gentlecorp.customer.service;

import com.gentlecorp.customer.MailProps;
import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.ContactExistsException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.IllegalArgumentException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.PasswordInvalidException;
import com.gentlecorp.customer.exception.UsernameExistsException;
import com.gentlecorp.customer.exception.VersionInvalidException;
import com.gentlecorp.customer.exception.VersionOutdatedException;
import com.gentlecorp.customer.model.entity.Contact;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.interfaces.VersionedEntity;
import com.gentlecorp.customer.repository.ContactRepository;
import com.gentlecorp.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.gentlecorp.customer.model.enums.StatusType.ACTIVE;
import static com.gentlecorp.customer.util.Constants.LOWERCASE;
import static com.gentlecorp.customer.util.Constants.MIN_LENGTH;
import static com.gentlecorp.customer.util.Constants.NUMBERS;
import static com.gentlecorp.customer.util.Constants.SYMBOLS;
import static com.gentlecorp.customer.util.Constants.UPPERCASE;
import static com.gentlecorp.customer.util.Validation.validateContact;
import static com.gentlecorp.customer.util.VersionUtils.validateVersion;
import static java.util.Locale.GERMAN;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteService {

  private final CustomerReadService customerReadService;
  private final CustomerRepository customerRepository;
  private final ContactRepository contactRepository;
  private final MailService mailService;
  private final MailProps props;
  private final KeycloakService keycloakService;

  public Customer create(final Customer customer, final String password) {
    customer.setCustomer_state(ACTIVE);
    log.debug("create: customer={}", customer);
    log.debug("create: address={}", customer.getAddress());

    if (customerRepository.existsByEmail(customer.getEmail()))
      throw new EmailExistsException(customer.getEmail());

    final var username = customer.getUsername();
    customer.setUsername(username.toLowerCase(GERMAN));
    final var isUsernameExisting = customerRepository.existsByUsername(username);
    if (isUsernameExisting)
      throw new UsernameExistsException(username);

    if (!checkPassword(password)) {
      throw new PasswordInvalidException(password);
    }

    log.warn("create: customer={}", customer);
    final var customerDb = customerRepository.save(customer);
    log.warn("create: customerDb={}", customerDb);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    props.setTo(customerDb.getEmail());
    mailService.send(customerDb);

    final var role = switch (customer.getTierLevel()) {
      case 1 -> "BASIC";
      case 2 -> "ELITE";
      case 3 -> "SUPREME";
      default -> throw new IllegalArgumentException(customer.getTierLevel());
    };

    keycloakService.signIn(customer, password, role);

    log.debug("create: customerDb={}", customerDb);
    return customerDb;
  }

  public Customer update(final Customer customer, final UUID id, final int version, final Jwt jwt) {
    log.debug("update: customer={}", customer);
    log.debug("update: id={}, version={}", id, version);
    log.trace("update: No constraints violated");

    customer.setCustomer_state(ACTIVE);
    final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    final var userAndRole = customerReadService.validateJwtAndGetUsernameAndRole(jwt);
    final var validatedUsername = userAndRole.getLeft();
    final var validatedRole = userAndRole.getRight();

    if (!Objects.equals(customerDb.getUsername(), validatedUsername) && !Objects.equals(validatedRole, "ADMIN")) {
      throw new AccessForbiddenException(validatedRole);
    }
    validateVersion(version, customerDb);
    final var email = customer.getEmail();
    if (!Objects.equals(email, customerDb.getEmail()) && customerRepository.existsByEmail(email)) {
      log.error("update: email {} already exists", email);
      throw new EmailExistsException(email);
    }

    final var username = customer.getUsername();
    customer.setUsername(username.toLowerCase(GERMAN));
    final var isUsernameExisting = customerRepository.existsByUsername(username);
    if (isUsernameExisting && !username.equals(validatedUsername)) {
      log.error("update: username {} already exists", email);
      throw new UsernameExistsException(username);
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

  public void updatePassword(final Jwt jwt, final String password) {
    log.debug("updatePassword: jwt={}", jwt);
    if (!checkPassword(password)) {
      throw new PasswordInvalidException(password);
    }
    keycloakService.updatePassword(password, jwt);
  }

  public List<Contact> addContact(final UUID customerId, final Contact contact, final int version, final Jwt jwt) {
    log.debug("create: customerId={}, contact={}", customerId, contact);
    final var customerDb = customerReadService.findById(customerId, jwt, true);
    validateVersion(version, customerDb);
    validateContact(contact, customerDb);
    customerDb.getContacts().add(contact);
    return customerDb.getContacts();
  }

  public Contact updateContact(final UUID customerId, final UUID contactId, final int version, final Contact contact, final Jwt jwt) {
    log.debug("updateContact: customerId={}, contactId={}", customerId, contactId);
    final var customerDb = customerReadService.findById(customerId, jwt, true);
    validateContact(contact, customerDb, contactId);

    final var contactDb = customerDb.getContacts()
      .stream()
      .filter(foundContact -> foundContact.getId().equals(contactId))
      .findFirst()
      .orElseThrow(() -> new NotFoundException(contactId));

    validateVersion(version, contactDb);
    contactDb.set(contact);
    return contactRepository.save(contactDb);
  }

  public void deleteById(final UUID id, final int version, final String token) {
    log.debug("deleteById: id={}", id);
    final var customerDb = customerRepository.findById(id).orElseThrow(NotFoundException::new);
    validateVersion(version, customerDb);
    keycloakService.delete(token, customerDb.getUsername());
    customerRepository.delete(customerDb);
  }

  public void removeContact(final UUID customerId, final UUID contactId, final int version, final Jwt jwt) {
    log.debug("removeContact: customerId={}, contactId={}, version={}", customerId, contactId, version);
    final var customerDb = customerReadService.findById(customerId, jwt, true);

    final var contactDb = customerDb.getContacts().stream()
        .filter(contact -> contact.getId().equals(contactId))
          .findFirst()
            .orElseThrow(() -> new NotFoundException(contactId));

    validateVersion(version, contactDb);

    customerDb.getContacts()
      .stream()
      .filter(contact -> contact.getId().equals(contactId))
      .toList()
      .forEach(contact -> customerDb.getContacts().remove(contact));
  }

  @SuppressWarnings("ReturnCount")
  private boolean checkPassword(final CharSequence password) {
    if (password.length() < MIN_LENGTH) {
      return false;
    }
    if (!UPPERCASE.matcher(password).matches()) {
      return false;
    }
    if (!LOWERCASE.matcher(password).matches()) {
      return false;
    }
    if (!NUMBERS.matcher(password).matches()) {
      return false;
    }
    return SYMBOLS.matcher(password).matches();
  }
}
