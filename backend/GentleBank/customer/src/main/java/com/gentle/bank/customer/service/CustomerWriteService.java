package com.gentle.bank.customer.service;

import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.mail.Mailer;
import com.gentle.bank.customer.repository.CustomerRepository;
import com.gentle.bank.customer.service.exception.ConstraintViolationsException;
import com.gentle.bank.customer.service.exception.EmailExistsException;
import com.gentle.bank.customer.service.exception.NotFoundException;
import com.gentle.bank.customer.service.exception.VersionOutdatedException;
import com.gentle.bank.customer.util.MailProps;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public Customer create(final Customer customer) {
        log.debug("create: customer={}", customer);
        log.debug("create: address={}", customer.getAddress());


        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new EmailExistsException(customer.getEmail());


        final var customerDb = customerRepository.save(customer);
        log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

        props.setTo(customerDb.getEmail());
        mailer.send(customerDb);

        log.debug("create: customerDb={}", customerDb);
        return customerDb;
    }

    @Transactional
    public Customer update(final Customer customer, final UUID id, final int version) {
        log.debug("update: customer={}", customer);

        log.debug("update: id={}, version={}", id, version);

        log.trace("update: Keine Constraints verletzt");

        final var customerDb = customerRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

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

        return updatedCustomerDb;
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
