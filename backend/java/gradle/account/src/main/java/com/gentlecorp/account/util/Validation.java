package com.gentlecorp.account.util;

import com.gentlecorp.account.exception.AccessForbiddenException;
import com.gentlecorp.account.exception.AccountExistsException;
import com.gentlecorp.account.exception.UnauthorizedException;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.model.entity.Customer;
import com.gentlecorp.account.model.enums.AccountType;
import com.gentlecorp.account.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class Validation {
  private final JwtService jwtService;

  public void validateCustomerRole(final Customer customer, final Jwt jwt) {
    log.debug("Validating Customer Role");
    final var customerUsername = customer.username();
    final var usernameAndRole = validateJwtAndGetUsernameAndRole(jwt);
    final var tokenUsername = usernameAndRole.getLeft();
    final var tokenRole = usernameAndRole.getRight();
    final var valid = (tokenUsername.equals(customerUsername) || Objects.equals(tokenRole, "ADMIN") || Objects.equals(tokenRole, "USER"));
    if (!valid) {
      log.error(String.format("unzureichende rolle als %s", tokenRole));
      throw new AccessForbiddenException(tokenRole);
    }
  }

  public void validateCustomerRole(final String username, final Jwt jwt) {
    log.debug("Validating Customer");
    final var usernameAndRole = validateJwtAndGetUsernameAndRole(jwt);
    final var tokenUsername = usernameAndRole.getLeft();
    final var tokenRole = usernameAndRole.getRight();
    final var valid = (tokenUsername.equals(username));
    if (!valid) {
      log.error(String.format("nur für den benutzer %s", username));
      throw new AccessForbiddenException(username,tokenRole);
    }
  }

  public void validateRole(final Jwt jwt) {
    log.debug("Validating Role");
    final var usernameAndRole = validateJwtAndGetUsernameAndRole(jwt);
    final var tokenUsername = usernameAndRole.getLeft();
    final var tokenRole = usernameAndRole.getRight();
    final var valid = (tokenRole.equals("ADMIN"));
    if (!valid) {
      log.error(String.format("unzureichende rolle als %s", tokenRole));
      throw new AccessForbiddenException(tokenRole);
    }
  }

  public Pair<String, String> validateJwtAndGetUsernameAndRole(Jwt jwt) {
    log.debug("Validating Jwt");
    final var username = jwtService.getUsername(jwt);
    if (username == null) {
      log.error("Missing username in token");
      throw new UnauthorizedException("Missing username in token");
    }

    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Missing role in token");
      throw new UnauthorizedException("Missing role in token");
    }
    log.debug("Validating Jwt: username={}, role={}", username, role);
    return Pair.of(username, role);
  }

  public void validateAccountCategory(final Collection<Account> accounts, final AccountType accountType) {
    log.info("Validating account category");

    accounts.forEach(
      account -> {
        if (account.getCategory().equals(accountType)) {
          log.error("Unable to validate account category");
          throw new AccountExistsException(accountType);
        }
      }
    );
  }
}
