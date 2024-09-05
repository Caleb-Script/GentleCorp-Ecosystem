package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionWriteService {

  private final TransactionRepository transactionRepository;
  private final TransactionReadService transactionReadService;
  private final Validation validation;

  public Transaction create(final Transaction transaction, final Jwt jwt) {
    log.debug("create: transaction={}", transaction);
    final var sender = transaction.getSender();
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var account = transactionReadService.findAccountById(sender, accessToken);
    validation.validateCustomer(account.customerUsername(), jwt);

    //TODO mit account balannce
    final var transactionDb = transactionRepository.save(transaction);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    log.debug("create: transactionDb={}", transactionDb);
    return transactionDb;
  }
}
