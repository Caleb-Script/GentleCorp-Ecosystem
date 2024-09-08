package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exception.InsufficientFundsException;
import com.gentlecorp.transaction.exception.InvalidTransactionException;
import com.gentlecorp.transaction.model.dto.BalanceDTO;
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
import java.util.Optional;
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

  private Boolean validateTransaction(final Transaction transaction) {
    log.debug("Validating transaction {}", transaction);

    if (transaction.getSender() == null || transaction.getReceiver() == null) {
      return false;
    }

    if (transaction.getSender().equals(transaction.getReceiver())) {
      log.error("Sender and Receiver are the same");
      //TODO passenden Namen für die Exception
      throw new InvalidTransactionException(transaction.getSender());
    }

    String adminAccessToken = String.format("Bearer %s", keycloakService.login("admin", "p").accessToken());
    transactionReadService.findAccountById(transaction.getReceiver(), adminAccessToken);
    return true;
  }

  public Transaction create(final Transaction transaction, final Jwt jwt) {
    log.debug("Creating transaction {}", transaction);
    var validationResult = validateTransaction(transaction);

    calculateBalance(transaction, validationResult, jwt);

    var savedTransaction = transactionRepository.save(transaction);
    log.debug("Transaction saved: {}", savedTransaction);
    return savedTransaction;
  }

  public Transaction create(final Transaction transaction) {
    log.debug("Creating transaction {}", transaction);

    sendBalanceAdjustment(transaction.getSender(), transaction.getAmount().negate());
    sendBalanceAdjustment(transaction.getReceiver(), transaction.getAmount());

    var savedTransaction = transactionRepository.save(transaction);
    log.debug("Transaction saved: {}", savedTransaction);
    return savedTransaction;
  }

  private void calculateBalance(final Transaction transaction, final boolean isTransaction, final Jwt jwt) {
    log.debug("Calculating balance for transaction: {}", transaction);
    BigDecimal transactionAmount = transaction.getAmount();

    if (transactionAmount.compareTo(BigDecimal.ZERO) == 0) {
      throw new IllegalArgumentException("Transaction amount cannot be zero.");
    }
    final var senderId = transaction.getSender();
    final var receiverId = transaction.getReceiver();

    if (isTransaction) {
      handleSenderReceiverBalance(senderId, receiverId, transactionAmount, jwt);
    } else {
      handleSingleAccountBalance(senderId, receiverId, transactionAmount, jwt);
    }
  }


  private void handleSenderReceiverBalance(final UUID senderId, final UUID receiverId, final BigDecimal transactionAmount, final Jwt jwt) {
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var senderAccount = transactionReadService.findAccountById(senderId, accessToken);
    validation.validateCustomer(senderAccount.customerUsername(), jwt);
    final BigDecimal newSenderBalance = senderAccount.balance().subtract(transactionAmount);

    if (newSenderBalance.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Insufficient funds for transaction. Sender balance: {}", newSenderBalance);
      throw new InsufficientFundsException();
    }

    sendBalanceAdjustment(senderId, transactionAmount.negate());
    sendBalanceAdjustment(receiverId, transactionAmount);
  }

  private void handleSingleAccountBalance(final UUID senderId, final UUID receiverId, final BigDecimal transactionAmount, final Jwt jwt) {
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final UUID accountId = Optional.ofNullable(senderId).orElse(receiverId);
    var account = transactionReadService.findAccountById(accountId, accessToken);
    validation.validateCustomer(account.customerUsername(), jwt);
    final BigDecimal adjustedAmount = accountId.equals(receiverId) ? transactionAmount.negate() : transactionAmount;
    sendBalanceAdjustment(accountId, adjustedAmount);
  }


  private void sendBalanceAdjustment(UUID accountId, BigDecimal amount) {
    final BalanceDTO balanceDTO = new BalanceDTO(accountId, amount);
    kafkaTemplate.send("adjustBalance", balanceDTO);
    log.debug("Balance adjustment sent: {}", balanceDTO);
  }
}
