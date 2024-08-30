package com.gentle.bank.customer.service;

import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.repository.CustomerRepository;
import com.gentle.bank.customer.repository.SpecificationBuilder;
import com.gentle.bank.customer.exception.AccessForbiddenException;
import com.gentle.bank.customer.exception.NotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Service class for managing read access to customer data.
 * <p>
 * This class provides methods for retrieving customer information from the database
 * based on various search criteria and for validating access to customer data
 * based on user roles.
 * </p>
 * <p>
 * All methods in this class are read-only and support transactions only in read mode.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {

  private final CustomerRepository customerRepository;
  private final SpecificationBuilder specificationBuilder;

  /**
   * Finds a customer by the specified ID.
   * <p>
   * This method checks whether the requested customer is either the requesting user's own
   * customer or if the requester has sufficient permissions (ADMIN or USER) to access
   * the customer data. If the customer is not found or access is not allowed, appropriate
   * exceptions are thrown.
   * </p>
   *
   * @param id       The ID of the customer to be retrieved.
   * @param username The username of the requesting user.
   * @param role     The role of the requesting user (e.g., "ADMIN", "USER").
   * @return The found customer.
   * @throws NotFoundException         If the customer is not found.
   * @throws AccessForbiddenException  If access to the customer data is not allowed.
   */
  public @NonNull Customer findById(
    final UUID id,
    final String username,
    final String role
  ) {
    log.debug("findById: id={}, username={}, role={}", id, username, role);

    final var customer = customerRepository.findById(id)
      .orElseThrow(NotFoundException::new);

    if (customer != null && customer.getUsername().contentEquals(username)) {
      // Own customer data
      return customer;
    }

    if (!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      // Not an admin, but not own (or no) customer data
      throw new AccessForbiddenException(role);
    }

    if (customer == null) {
      throw new NotFoundException(id);
    }

    log.debug("findById: customer={}", customer);
    return customer;
  }

  /**
   * Searches for customers based on the provided search criteria.
   * <p>
   * The method uses the provided search criteria to build a query for the
   * customer repository. If no search criteria are provided, all customers
   * from the database are returned. If no customers are found, an exception is thrown.
   * </p>
   *
   * @param searchCriteria The search criteria as a map of key-value pairs.
   * @return A collection of found customers or an empty list if none are found.
   * @throws NotFoundException If no customers are found.
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
