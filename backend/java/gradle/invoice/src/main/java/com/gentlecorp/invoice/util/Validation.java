package com.gentlecorp.invoice.util;


import com.gentlecorp.invoice.exception.AccessForbiddenException;
import com.gentlecorp.invoice.exception.InsufficientFundsException;
import com.gentlecorp.invoice.exception.InvoiceAlreadyPaidException;
import com.gentlecorp.invoice.exception.UnauthorizedException;
import com.gentlecorp.invoice.model.entity.Account;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

import static com.gentlecorp.invoice.model.enums.StatusType.PAID;

@Component
@Slf4j
@RequiredArgsConstructor
public class Validation {
  private final JwtService jwtService;

  public void validateCustomerRole(final Account account, final Jwt jwt) {
    log.debug("Validating Customer Role for account: {}",account);
    final var customerUsername = account.customerUsername();
    final var usernameAndRole = validateJwtAndGetUsernameAndRole(jwt);
    final var tokenUsername = usernameAndRole.getLeft();
    final var tokenRole = usernameAndRole.getRight();
    final var valid = (tokenUsername.equals(customerUsername) || Objects.equals(tokenRole, "ADMIN") || Objects.equals(tokenRole, "USER"));
    if (!valid) {
      log.error("Invalid Customer: username={}, role={}", customerUsername, tokenRole);
      log.error(String.format("unzureichende rolle als %s", tokenRole));
      throw new AccessForbiddenException(tokenRole);
    }
  }

  public void validateCustomer(final String username, final Jwt jwt) {
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

  public void validatePayment(final Invoice invoice, final Payment payment, final BigDecimal currentBalance) {
    log.debug("validatePayment: invoice={}", invoice);

    if (invoice.getAmountLeft().compareTo(BigDecimal.ZERO) == 0) {
      invoice.setType(PAID);
    }

    if (invoice.getType().equals(PAID)) {
      log.error("pay: Invoice already paid");
      throw new InvoiceAlreadyPaidException(invoice.getId());
    }

    if (payment.getAmount().compareTo(invoice.getAmountLeft()) > 0) {
      log.warn("pay: Payment amount exceeds payment limit");
      payment.setAmount(invoice.getAmountLeft());
    }

    if (currentBalance.compareTo(payment.getAmount()) < 0) {
      throw new InsufficientFundsException(invoice.getAccountId());
    }

  }
}
