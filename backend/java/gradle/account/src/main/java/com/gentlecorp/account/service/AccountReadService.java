package com.gentlecorp.account.service;

import com.gentlecorp.account.exception.AccessForbiddenException;
import com.gentlecorp.account.exception.UnauthorizedException;
import com.gentlecorp.account.model.entity.Customer;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.repository.AccountRepository;
import com.gentlecorp.account.exception.NotFoundException;
import com.gentlecorp.account.repository.CustomerRepository;
import com.gentlecorp.account.repository.SpecificationBuilder;
import com.gentlecorp.account.util.Validation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AccountReadService {
  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;
  private final SpecificationBuilder specificationBuilder;
  private final Validation validation;

  public @NonNull Account findById(
    final UUID id,
    final Jwt jwt
    ) {
    log.debug("Find account by id: {}", id);
    final var userAndRole = validation.validateJwtAndGetUsernameAndRole(jwt);
    final var username = userAndRole.getLeft();
    final var role = userAndRole.getRight();
    final var token = String.format("Bearer %s", jwt.getTokenValue());

    log.debug("findById: id={}, username={}, role={}", id, username, role);
    final var account = accountRepository.findById(id).orElseThrow(NotFoundException::new);

    if (account == null) {
      throw new NotFoundException(id);
    }
    final var customer = findCustomerById(account.getCustomerId(), token);
    account.setCustomerUsername(customer.username());

    if (customer.username().equals(username)) {
      return account;
    }

    if (!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      throw new AccessForbiddenException(role);
    }
    log.debug("findById: account={}", account);
    return account;
  }

  public @NonNull Collection<Account> find(@NonNull final Map<String, List<String>> searchCriteria, final String token) {
    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      final var accounts = accountRepository.findAll();

      accounts.forEach(account -> {
        final var customer = findCustomerById(account.getCustomerId(), token);
        final var customerUsername = customer.username();
        account.setCustomerUsername(customerUsername);
      });

      return accounts;
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var accounts = accountRepository.findAll(specification);

    if (accounts.isEmpty())
      throw new NotFoundException(searchCriteria);

    accounts.forEach(account -> {
      final var customer = findCustomerById(account.getCustomerId(), token);
      final var customerUsername = customer.username();
      account.setCustomerUsername(customerUsername);
    });

    log.debug("find: accounts={}", accounts);
    return accounts;
  }

  public Collection<Account> findByCustomerId(final UUID customerId, final Jwt jwt) {
    log.debug("findByCustomerId: customerId={}", customerId);
    final var token = String.format("Bearer %s", jwt.getTokenValue());
    final var userAndRole = validation.validateJwtAndGetUsernameAndRole(jwt);
    final var username = userAndRole.getLeft();
    final var role = userAndRole.getRight();

    log.debug("findByCustomerId: customerId={}", customerId);

    final var accounts = accountRepository.findByCustomerId(customerId);
    if (accounts.isEmpty()) {
      log.error("findByCustomerId: no accounts found for user={}", username);
      throw new NotFoundException();
    }

    final var customer = findCustomerById(customerId, token);
    final var customerUsername = customer.username();
    log.trace("findByCustomerId: customerUsername={}", customerUsername);

    accounts.forEach(account -> {
      account.setCustomerUsername(customerUsername);
    });

    if (customer.username().equals(username)) {
      return accounts;
    }

    if (!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      throw new AccessForbiddenException(role);
    }
    log.debug("findByCustomerId: accounts={}", accounts);
    return accounts;
  }

  @SuppressWarnings("ReturnCount")
  Customer findCustomerById(final UUID customerId, final String token) {
    log.debug("findCustomerById: customerId={}", customerId);

    final Customer customer;
    try {
      customer = customerRepository.getById(customerId.toString(), token);
    } catch (final HttpClientErrorException.NotFound ex) {
      // Statuscode 404
      log.debug("findCustomerById: HttpClientErrorException.NotFound");
      return new Customer("N/A");
    } catch (final HttpStatusCodeException ex) {
      // sonstiger Statuscode 4xx oder 5xx
      // HttpStatusCodeException oder RestClientResponseException (z.B. ServiceUnavailable)
      log.debug("findCustomerById", ex);
      return new Customer("Exception");
    }

    log.debug("findCustomerById: {}", customer);
    return customer;
  }

  public BigDecimal getFullBalance(final UUID customerId, final Jwt jwt) {
    log.debug("getBalanceFromAccountById: customerId={}", customerId);
    final var accountList = findByCustomerId(customerId, jwt);

    final var fullBalance = accountList.stream()
      .map(Account::getBalance)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.debug("getBalanceFromAccountById: fullBalance={}", fullBalance);
    return fullBalance;
  }
}
