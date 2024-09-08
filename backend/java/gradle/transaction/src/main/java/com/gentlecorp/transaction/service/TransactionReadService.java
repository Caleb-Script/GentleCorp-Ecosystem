package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exception.AccessForbiddenException;
import com.gentlecorp.transaction.exception.NotFoundException;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.entity.Account;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.repository.AccountRepository;
import com.gentlecorp.transaction.repository.SpecificationBuilder;
import com.gentlecorp.transaction.util.Validation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.gentlecorp.transaction.model.enums.TransactionType.DEPOSIT;
import static com.gentlecorp.transaction.model.enums.TransactionType.INCOME;
import static com.gentlecorp.transaction.model.enums.TransactionType.PAYMENT;
import static com.gentlecorp.transaction.model.enums.TransactionType.REFUND;
import static com.gentlecorp.transaction.model.enums.TransactionType.TRANSFER;
import static com.gentlecorp.transaction.model.enums.TransactionType.WITHDRAWAL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TransactionReadService {
  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final SpecificationBuilder specificationBuilder;
  private final Validation validation;

  private void setTransactionType(final Collection<Transaction> transactions, final UUID accountId) {
    log.debug("setTransactionType: accountId={}", accountId);
    UUID zeroUUID = UUID.fromString("30000000-0000-0000-0000-000000000000");

    transactions.forEach(transaction -> {
      final var sender = transaction.getSender();
      final var receiver = transaction.getReceiver();

      // Payment and Refund checks first to avoid overwriting
      if (zeroUUID.equals(receiver)) {
        transaction.setType(PAYMENT);
        return;
      }

      if (zeroUUID.equals(sender)) {
        transaction.setType(REFUND);
        return;
      }

      if (accountId.equals(sender)) {
        if (receiver == null) {
          transaction.setType(DEPOSIT);
        } else {
          transaction.setType(TRANSFER);
        }
        return;
      }

      if (accountId.equals(receiver)) {
        if (sender == null) {
          transaction.setType(WITHDRAWAL);
        } else {
          transaction.setType(INCOME);
        }
      }
    });
  }

  public @NonNull Transaction findById(final UUID id, final Jwt jwt) {
    log.debug("Find transaction by id: {}", id);
    final var transaction = transactionRepository.findById(id).orElseThrow(NotFoundException::new);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var account = findAccountById(transaction.getReceiver(), accessToken);
    validation.validateCustomerRole(account, jwt);
    log.debug("findById: transaction={}", transaction);
    return transaction;
  }

  public @NonNull Collection<Transaction> find(@NonNull final Map<String, List<String>> searchCriteria, final String token) {
    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      return transactionRepository.findAll();
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var transactions = transactionRepository.findAll(specification);

    if (transactions.isEmpty())
      throw new NotFoundException(searchCriteria);
    log.debug("find: transactions={}", transactions);
    return transactions;
  }

  public Collection<Transaction> findByAccountId(final UUID accountId,final Jwt jwt) {
    log.debug("findByAccountId: accountId={}", accountId);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var transactions = transactionRepository.findByAccountId(accountId);
    if (transactions.isEmpty()) {
      throw new NotFoundException();
    }
    final var account = findAccountById(accountId, accessToken);
    final var accountUsername = account.customerUsername();
    log.trace("findByAccountId: accountUsername={}", accountUsername);
    validation.validateCustomerRole(account,jwt);
    setTransactionType(transactions,accountId);
    log.debug("findByAccountId: transactions={}", transactions);
    return transactions;
  }

  @SuppressWarnings("ReturnCount")
  public Account findAccountById(final UUID accountId, final String token) {
    log.debug("findAccountById: accountId={}, token={}", accountId, token);

    final Account account;
    try {
      account = accountRepository.getById(accountId.toString(), "1", token).getBody();
    } catch (final HttpClientErrorException.Unauthorized ex) {
      log.error("Unauthorized access attempt with token: {}", token);
      throw new AccessForbiddenException("User does not have the required permissions.",1);
    } catch (final HttpClientErrorException.Forbidden ex) {
      log.error("Access forbidden with token: {}", token);
      throw new AccessForbiddenException("User role is not permitted.",1);
    } catch (final HttpClientErrorException.NotFound ex) {
      log.debug("No account found for ID: {}", accountId);
      throw new NotFoundException(accountId, accountId);
    } catch (final HttpStatusCodeException ex) {
      log.error("HTTP error while finding account with ID={}: Status code={}, Message={}", accountId, ex.getStatusCode(), ex.getMessage());
      throw new RuntimeException("Unexpected error while processing the request.");
    }

    log.debug("Account found: {}", account);
    return account;
  }
}
