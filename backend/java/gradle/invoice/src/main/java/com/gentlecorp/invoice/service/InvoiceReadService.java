package com.gentlecorp.invoice.service;

import com.gentlecorp.invoice.exception.AccessForbiddenException;
import com.gentlecorp.invoice.exception.NotFoundException;
import com.gentlecorp.invoice.model.entity.Account;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.repository.AccountRepository;
import com.gentlecorp.invoice.repository.SpecificationBuilder;
import com.gentlecorp.invoice.repository.InvoiceRepository;
import com.gentlecorp.invoice.util.Validation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class InvoiceReadService {
  private final InvoiceRepository invoiceRepository;
  private final AccountRepository accountRepository;
  private final SpecificationBuilder specificationBuilder;
  private final Validation validation;

  public @NonNull Invoice findById(final UUID id, final Jwt jwt) {
    log.debug("findById: id={}", id);
    final var invoice = invoiceRepository.findById(id).orElseThrow(NotFoundException::new);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var account = findAccountById(invoice.getAccountId(), accessToken, false).stream().findFirst().orElseThrow(NotFoundException::new);
    validation.validateCustomerRole(account, jwt);
    final var alreadyPaid = calculatePaymentInfo(invoice);
    invoice.setAmountLeft(invoice.getAmount().subtract(alreadyPaid));
    log.debug("findById: invoice={}", invoice);
    return invoice;
  }

  public @NonNull Collection<Invoice> find(@NonNull final Map<String, List<String>> searchCriteria, final String token) {
    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      return invoiceRepository.findAll();
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var invoices = invoiceRepository.findAll(specification);

    if (invoices.isEmpty())
      throw new NotFoundException(searchCriteria);

    log.debug("find: invoices={}", invoices);
    return invoices;
  }

  public @NonNull Collection<Invoice> findByAccountId(final UUID accountId,final Jwt jwt) {
    log.debug("findByAccountId: accountId={}", accountId);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var invoices = invoiceRepository.findByAccountId(accountId);
    if (invoices.isEmpty()) {
      throw new NotFoundException();
    }
    final var account = findAccountById(accountId, accessToken, false).stream().findFirst().orElseThrow(NotFoundException::new);
    final var accountUsername = account.customerUsername();
    log.trace("findByAccountId: accountUsername={}", accountUsername);
    validation.validateCustomerRole(account,jwt);
    log.debug("findByAccountId: invoices={}", invoices);
    if (invoices.isEmpty())
      throw new NotFoundException(accountId);
    return invoices;
  }

  @SuppressWarnings("ReturnCount")
  Collection<Account> findAccountById(final UUID accountId, final String token, final boolean isCustomer) {
    log.debug("findAccountById: accountId={}", accountId);

    final Collection<Account> accounts;
    try {
      accounts = isCustomer
        ? accountRepository.getByCustomerId(accountId.toString(), token)
        : Collections.singleton(accountRepository.getById(accountId.toString(), token));
    } catch (final HttpClientErrorException.Unauthorized ex) {
      log.error("Unauthorized access attempt with token: {}", token);
      throw new AccessForbiddenException("User does not have the required permissions.",1);
    } catch (final HttpClientErrorException.Forbidden ex) {
      log.error("Access forbidden with token: {}", token);
      throw new AccessForbiddenException("User role is not permitted.",1);
    } catch (final HttpClientErrorException.NotFound ex) {
      // Statuscode 404
      log.debug("findAccountById: HttpClientErrorException.NotFound");
      throw new NotFoundException(accountId,accountId);
    } catch (final HttpStatusCodeException ex) {
      // sonstiger Statuscode 4xx oder 5xx
      // HttpStatusCodeException oder RestClientResponseException (z.B. ServiceUnavailable)
      log.debug("findAccountById", ex);
      throw new RestClientException("Http Status Code: " + ex.getStatusCode(), ex);
    }

    log.debug("findAccountById: {}", accounts);
    return accounts;
  }

  public Collection<BigDecimal> findPaymentsByInvoiceId(final UUID invoiceId, final Jwt jwt) {
    log.debug("findPaymentsByInvoiceId: invoiceId={}", invoiceId);
    final var invoice = findById(invoiceId, jwt);
    final var payments = invoice.getPayments();
    return  payments.stream().map(Payment::getAmount).toList();
  }

  public Collection<Invoice> findByCustomerId(final UUID customerId, final Jwt jwt) {
    log.debug("findByCustomerId: customerId={}", customerId);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var accountList = findAccountById(customerId, accessToken, true);
    log.debug("findByCustomerId: accountList={}", accountList);
    final var dummyAccount = accountList.stream().findFirst().orElseThrow(NotFoundException::new);
    validation.validateCustomerRole(dummyAccount, jwt);

    final var invoiceList = accountList
      .stream()
      .map(account -> {
        try {
          return Optional.of(findByAccountId(account.id(), jwt));
        } catch (NotFoundException e) {
          log.error("No Invoices to the Accouont ID: {}",account.id(), e);
          return Optional.<Collection<Invoice>>empty();
        }
      })
      .filter(Optional::isPresent) // Entfernt alle leeren Optional-Objekte
      .map(Optional::get) // Holt die eigentlichen Werte aus den Optional-Objekten
      .flatMap(Collection::stream)
      .toList();

    log.debug("findByCustomerId: invoices={}", invoiceList);
    return invoiceList;
  }

  public BigDecimal calculatePaymentInfo(final Invoice invoice) {
    final var alreadyPayments = invoice.getPayments().stream()
      .map(Payment::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
    log.debug("calculatePaymentInfo: alreadyPayments={}", alreadyPayments);
    return alreadyPayments;
  }

  public BigDecimal findCurrentBalanceByAccountId(final UUID accountId, final String token) {
    log.debug("findCurrentBalanceByAccountId: accountId={}", accountId);
    final var currentBalance = accountRepository.getBalanceById(accountId.toString(),"\"-1\"", token).getBody();
    log.debug("findCurrentBalanceByAccountId: currentBalance={}", currentBalance);
    return currentBalance;
  }
}
