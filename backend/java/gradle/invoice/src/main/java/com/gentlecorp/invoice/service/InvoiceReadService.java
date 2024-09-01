package com.gentlecorp.invoice.service;

import com.gentlecorp.invoice.exception.AccessForbiddenException;
import com.gentlecorp.invoice.exception.NotFoundException;
import com.gentlecorp.invoice.model.entity.Account;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.repository.AccountRepository;
import com.gentlecorp.invoice.repository.SpecificationBuilder;
import com.gentlecorp.invoice.repository.InvoiceRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class InvoiceReadService {
  private final InvoiceRepository invoiceRepository;
  private final AccountRepository accountRepository;
  private final SpecificationBuilder specificationBuilder;

  public @NonNull Invoice findById(
    final UUID id,
    final String username,
    final String role,
    final String token
    ) {
    log.debug("findById: id={}, customerUsername={}, role={}", id, username, role);
    final var invoice = invoiceRepository.findById(id).orElseThrow(NotFoundException::new);

    if (invoice == null) {
      throw new NotFoundException(id);
    }
    invoice.setAmountLeft(invoice.getAmount().subtract(calculatePaymentInfo(invoice)));
    log.debug("findById: amountLeft={}", invoice.getAmountLeft());
    final var account = findAccountById(invoice.getAccountId(), token);

    if (account.customerUsername().contentEquals(username)) {
      return invoice;
    }

    if (!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      throw new AccessForbiddenException(role);
    }
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

  public Collection<Invoice> findByAccountId(final UUID accountId,final String role, final String username, final String token) {
    log.debug("findByAccountId: accountId={}", accountId);

    final var invoices = invoiceRepository.findByAccountId(accountId);
    if (invoices.isEmpty()) {
      throw new NotFoundException();
    }
    final var account = findAccountById(accountId, token);
    final var accountUsername = account.customerUsername();
    log.trace("findByAccountId: accountUsername={}", accountUsername);

    if (account.customerUsername().contentEquals(username)) {
      return invoices;
    }

    if(!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      throw new AccessForbiddenException(accountUsername);
    }

    log.debug("findByAccountId: invoices={}", invoices);
    return invoices;
  }

  @SuppressWarnings("ReturnCount")
  private Account findAccountById(final UUID accountId, final String token) {
    log.debug("findAccountById: accountId={}", accountId);

    final Account account;
    try {
      account = accountRepository.getById(accountId.toString(), token);
    } catch (final HttpClientErrorException.NotFound ex) {
      // Statuscode 404
      log.debug("findAccountById: HttpClientErrorException.NotFound");
      return new Account("N/A");
    } catch (final HttpStatusCodeException ex) {
      // sonstiger Statuscode 4xx oder 5xx
      // HttpStatusCodeException oder RestClientResponseException (z.B. ServiceUnavailable)
      log.debug("findAccountById", ex);
      return new Account("Exception");
    }

    log.debug("findAccountById: {}", account);
    return account;
  }

  public Collection<BigDecimal> findPaymentsByInvoiceId(final UUID invoiceId, final String username, final String role, final String token) {
    log.debug("findPaymentsByInvoiceId: invoiceId={}", invoiceId);

    final var invoice = findById(invoiceId, username, role, token);
    final var payments = invoice.getPayments();
    return  payments.stream().map(Payment::getAmount).toList();
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
    final var currentBalance = accountRepository.getBalanceById(accountId.toString(), token);
    log.debug("findCurrentBalanceByAccountId: currentBalance={}", currentBalance);
    return currentBalance.getBody();
  }
}
