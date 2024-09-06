package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exception.InsufficientFundsException;
import com.gentlecorp.transaction.model.dto.BalanceDTO;
import com.gentlecorp.transaction.model.entity.Account;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionWriteService {

  private final TransactionRepository transactionRepository;
  private final TransactionReadService transactionReadService;
  private final Validation validation;
  private final KafkaTemplate<String, BalanceDTO> kafkaTemplate;
  private final KeycloakService keycloakService;

  private Pair<Account, String> validateTransaction(final Transaction transaction, final Jwt jwt) {
    log.debug("Validating transaction {}", transaction);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var senderAccount = transactionReadService.findAccountById(transaction.getSender(), accessToken);
    final var adminToken = keycloakService.login("admin", "p");
    final var adminAccessToken = String.format("Bearer %s", adminToken.accessToken());
    transactionReadService.findAccountById(transaction.getReceiver(), adminAccessToken);
    validation.validateCustomer(senderAccount.customerUsername(), jwt);
    return Pair.of(senderAccount, accessToken);
  }

  public Transaction create(final Transaction transaction, final Jwt jwt) {
    log.debug("create: transaction={}", transaction);
    final var accessTokenAndAccount = validateTransaction(transaction, jwt);
    final var senderAccount = accessTokenAndAccount.getLeft();
    final var accessToken = accessTokenAndAccount.getRight();
    log.debug("create: senderAccount={}", senderAccount);
    calcuteBalance(transaction, accessToken);
    final var transactionDb = transactionRepository.save(transaction);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());
    log.debug("create: transactionDb={}", transactionDb);
    return transactionDb;
  }

  private void calcuteBalance(final Transaction transaction, final String accessToken) {
    log.debug("calcuteBalance: transaction={}", transaction);
    final var senderId = transaction.getSender();
    final var receiverId = transaction.getReceiver();
    final var accountSender = transactionReadService.findAccountById(senderId, accessToken);
    final var senderBalance = accountSender.balance();
    final var newSenderBalance = senderBalance.subtract(transaction.getAmount());
    if (newSenderBalance.compareTo(BigDecimal.ZERO) < 0) {
      log.error("calcuteBalance: newSenderBalance={}", newSenderBalance);
      throw new InsufficientFundsException();
    }
    final var sender = new BalanceDTO(senderId, transaction.getAmount().negate());
    final var receiver = new BalanceDTO(receiverId, transaction.getAmount());
    log.debug("calcuteBalance: sender={}, receiver={}", sender, receiver);
    kafkaTemplate.send("adjustBalance", sender);
    kafkaTemplate.send("adjustBalance", receiver);
  }
}
