package com.gentlecorp.customer.service;

import com.gentlecorp.customer.exception.AccessForbiddenException;
import com.gentlecorp.customer.exception.NotFoundException;
import com.gentlecorp.customer.exception.UnauthorizedException;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.repository.CustomerRepository;
import com.gentlecorp.customer.repository.SpecificationBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomerReadService {
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;
  private final SpecificationBuilder specificationBuilder;


  Pair<String, String> validateJwtAndGetUsernameAndRole(Jwt jwt) {
    final var username = jwtService.getUsername(jwt);
    if (username == null) {
      throw new UnauthorizedException("Missing username in token");
    }

    final var role = jwtService.getRole(jwt);
    if (role == null) {
      throw new UnauthorizedException("Missing role in token");
    }

    return Pair.of(username, role);
  }

  public @NonNull Customer findById(
    final UUID id,
    final Jwt jwt,
    final boolean fetchAll
  ) {
    log.debug("findById: id={}, fetchAll={}", id, fetchAll);

    final var userAndRole = validateJwtAndGetUsernameAndRole(jwt);
    final var username = userAndRole.getLeft();
    final var role = userAndRole.getRight();

    final var customer = fetchAll
      ? customerRepository.findByIdFetchAll(id).orElseThrow(NotFoundException::new)
      : customerRepository.findById(id).orElseThrow(NotFoundException::new);

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

  public @NonNull Collection<Customer> find(@NonNull final Map<String, List<String>> searchCriteria) {

    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      return customerRepository.findAll();
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var customers = customerRepository.findAll(specification);

//    if (customers.isEmpty())
//      throw new NotFoundException(searchCriteria);

    log.debug("find: customers={}", customers);
    return customers;
  }
}
