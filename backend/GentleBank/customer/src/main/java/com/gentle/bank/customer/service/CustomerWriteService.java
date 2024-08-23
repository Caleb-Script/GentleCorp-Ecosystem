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
 * Anwendungslogik für Kunden auch mit Bean Validation.
 * <img src="../../../../../asciidoc/CustomerWriteService.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteService {
    private final CustomerRepository customerRepository;
    private final Mailer mailer;
    private final MailProps props;
    private final KeycloakService keycloakService;

    @Transactional
    public Customer create(final Customer customer, final String password, final Jwt jwt) {
        log.debug("create: customer={}", customer);
        log.debug("create: address={}", customer.getAddress());


        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new EmailExistsException(customer.getEmail());


        final var customerDb = customerRepository.save(customer);
        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        props.setTo(customerDb.getEmail());
        mailer.send(customerDb);

        final var role = customer.isElite()
          ? "gentlecorp-elite"
          : "gentlecorp-essential";

        keycloakService.signIn(customer, password, role);

        log.debug("create: customerDb={}", customerDb);
        return customerDb;
    }

    @Transactional
    public Customer update(final Customer customer, final UUID id, final int version, final String role, final String username, final Jwt jwt) {
        log.debug("update: customer={}", customer);
        log.debug("update: id={}, version={}", id, version);
        log.trace("update: Keine Constraints verletzt");

        final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

        if (!Objects.equals(customerDb.getUsername(), username) && !Objects.equals(role,"ADMIN")) {
          throw new AccessForbiddenException("DU BIST NICHT DER USER");
        }

        if (version != customerDb.getVersion()) {
            log.error("version ist nicht die Aktuelle Verion");
            throw new VersionOutdatedException(version);
        }

        final var email = customer.getEmail();
        if (!Objects.equals(email, customerDb.getEmail()) && customerRepository.existsByEmail(email)) {
            log.error("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }
        log.trace("update: Kein Konflikt mit der Emailadresse");


        customerDb.set(customer);

        customerDb.setContactOptionsString(customer.getContactOptions());
        log.debug("NEW contactOptionsString: {}", customerDb.getContactOptionsString());

        final var updatedCustomerDb = customerRepository.save(customerDb);


        log.debug("update: updatedCustomerDB={}", customerDb);
        log.debug("update: updatedCustomer={}", customerDb);

      keycloakService.update(updatedCustomerDb,jwt);

        return updatedCustomerDb;
    }

    @Transactional
    public void updatePassword(final Jwt jwt, final String password) {
      log.debug("updatePassword: jwt={}", jwt);

      keycloakService.updatePassword(password,jwt);
    }

    /**
     * Einen Kunden löschen.
     *
     * @param id Die ID des zu löschenden Kunden.
     */
    @Transactional
    public void deleteById(final UUID id) {
        log.debug("deleteById: id={}", id);

        final var customer = customerRepository.findById(id).orElseThrow(NotFoundException::new);
        customerRepository.delete(customer);
    }
}
