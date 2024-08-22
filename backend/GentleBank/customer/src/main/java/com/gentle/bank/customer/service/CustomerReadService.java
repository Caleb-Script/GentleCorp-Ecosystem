package com.gentle.bank.customer.service;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.repository.CustomerRepository;
import com.gentle.bank.customer.repository.SpecificationBuilder;
import com.gentle.bank.customer.security.Rolle;
import com.gentle.bank.customer.service.exception.AccessForbiddenException;
import com.gentle.bank.customer.service.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.gentle.bank.customer.security.Rolle.GENTLECORP_ADMIN;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {
    private final CustomerRepository customerRepository;
    private final SpecificationBuilder specificationBuilder;

    public @NonNull Customer findById(
      final UUID id,
      final String username,
      final List<Rolle> rollen
    ) {
      log.debug("findById: id={}, username={}, rollen={}", id, username, rollen);

        final var customer = customerRepository.findById(id).orElseThrow(NotFoundException::new);

      if (customer != null && customer.getUsername().contentEquals(username)) {
        // eigene Gaestedaten
        return customer;
      }

      if (!rollen.contains(GENTLECORP_ADMIN)) {
        // nicht admin, aber keine eigenen (oder keine) Gaestedaten
        throw new AccessForbiddenException(rollen);
      }

      if (customer == null) {
        throw new NotFoundException(id);
      }

        log.debug("findById: customer={}", customer);
        return customer;
    }

    /**
     * Kunden anhand von Suchkriterien als Collection suchen.
     *
     * @param searchCriteria Die Suchkriterien
     * @return Die gefundenen Kunden oder eine leere Liste
     * @throws NotFoundException Falls keine Kunden gefunden wurden
     */
    public @NonNull Collection<Customer> find(@NonNull final Map<String, List<String>> searchCriteria) {
        log.debug("find: searchCriteria={}", searchCriteria);

        if (searchCriteria.isEmpty()) {
            return customerRepository.findAll();
        }

        final var specification = specificationBuilder
            .build(searchCriteria)
            .orElseThrow(() -> new NotFoundException(searchCriteria));
        final var customers = customerRepository.findAll(specification);

        if (customers.isEmpty())
            throw new NotFoundException(searchCriteria);

        log.debug("find: customers={}", customers);
        return customers;
    }
}
