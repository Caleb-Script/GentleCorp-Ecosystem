package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionWriteService {

  private final TransactionRepository transactionRepository;

  public Transaction create(final Transaction transaction) {
    log.debug("create: transaction={}", transaction);

    //TODO mit account balannce
    final var transactionDb = transactionRepository.save(transaction);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    log.debug("create: transactionDb={}", transactionDb);
    return transactionDb;
  }
}
