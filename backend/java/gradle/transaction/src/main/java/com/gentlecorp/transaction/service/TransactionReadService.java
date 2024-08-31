package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exception.AccessForbiddenException;
import com.gentlecorp.transaction.exception.NotFoundException;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.entity.Account;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.repository.AccountRepository;
import com.gentlecorp.transaction.repository.SpecificationBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

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

  public @NonNull Transaction findById(
    final UUID id,
    final String username,
    final String role,
    final String token
    ) {
    log.debug("findById: id={}, customerUsername={}, role={}", id, username, role);
    final var transaction = transactionRepository.findById(id).orElseThrow(NotFoundException::new);

    if (transaction == null) {
      throw new NotFoundException(id);
    }
//    final var account = findAccountById(transaction.getAccountId(), token);
//    transaction.setAccountUsername(account.customerUsername());
//
//    if (transaction.getAccountUsername().contentEquals(customerUsername)) {
//      return transaction;
//    }

    if (!Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
      throw new AccessForbiddenException(role);
    }
    log.debug("findById: transaction={}", transaction);
    return transaction;
  }

  public @NonNull Collection<Transaction> find(@NonNull final Map<String, List<String>> searchCriteria, final String token) {
    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      final var transactions = transactionRepository.findAll();

//      transactions.forEach(transaction -> {
//        final var account = findAccountById(transaction.getAccountId(), token);
//        final var accountUsername = account.customerUsername();
//        transaction.setAccountUsername(accountUsername);
//      });

      return transactions;
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var transactions = transactionRepository.findAll(specification);

    if (transactions.isEmpty())
      throw new NotFoundException(searchCriteria);

//    transactions.forEach(transaction -> {
//      final var account = findAccountById(transaction.getAccountId(), token);
//      final var accountUsername = account.customerUsername();
//      transaction.setAccountUsername(accountUsername);
//    });

    log.debug("find: transactions={}", transactions);
    return transactions;
  }

  public Collection<Transaction> findByAccountId(final UUID accountId,final String username, final String token) {
    log.debug("findByAccountId: accountId={}", accountId);

    final var transactions = transactionRepository.findByAccountId(accountId);
    if (transactions.isEmpty()) {
      throw new NotFoundException();
    }

    final var account = findAccountById(accountId, token);
    final var accountUsername = account.customerUsername();
    log.trace("findByAccountId: accountUsername={}", accountUsername);

//    if(!accountUsername.equals(username) && !Objects.equals(role, "ADMIN") && !Objects.equals(role, "USER")) {
//      throw new AccessForbiddenException(accountUsername);
//    }

    if(!accountUsername.equals(username)) {
      throw new AccessForbiddenException(accountUsername);
    }

    transactions.forEach(transaction -> {
        if(accountId.equals(transaction.getSender())) {
          if(transaction.getReceiver() == null) {
            transaction.setType(DEPOSIT);
          } else {
            transaction.setType(TRANSFER);
          }
        }

      if(accountId.equals(transaction.getReceiver())) {
        if (transaction.getSender() == null) {
          transaction.setType(WITHDRAWAL);
        } else {
          transaction.setType(INCOME);
        }
      }

      if(transaction.getReceiver().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
        transaction.setType(PAYMENT);
      }

      if(transaction.getSender().equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
        transaction.setType(REFUND);
      }
      });

    log.debug("findByAccountId: transactions={}", transactions);
    return transactions;
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
}
