package com.gentlecorp.account.service;

import com.gentlecorp.account.exception.AccessForbiddenException;
import com.gentlecorp.account.exception.InsufficientFundsException;
import com.gentlecorp.account.exception.NotFoundException;
import com.gentlecorp.account.model.dto.BalanceDTO2;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.repository.AccountRepository;
import com.gentlecorp.account.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static com.gentlecorp.account.model.enums.StatusType.ACTIVE;
import static com.gentlecorp.account.model.enums.StatusType.CLOSED;
import static com.gentlecorp.account.util.VersionUtils.validateVersion;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountWriteService {

  private final AccountReadService accountReadService;
  private final AccountRepository accountRepository;
  private final Validation validation;

  public Account create(final Account account, final Jwt jwt) {
    log.debug("Creating account {}", account);
    final var token = String.format("Bearer %s", jwt.getTokenValue());
    final var customerId = account.getCustomerId();
    final var customer = accountReadService.findCustomerById(customerId, token);
    final var existingAccounts = accountReadService.findByCustomerId(customerId,jwt);
    validation.validateAccountCategory(existingAccounts, account.getCategory());
    account.setState(ACTIVE);
    log.debug("create: account={}", account);
    validation.validateCustomerRole(customer, jwt);
    final var accountDb = accountRepository.save(account);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());
    log.debug("create: accountDb={}", accountDb);
    return accountDb;
  }

  public Account processTransaction(final UUID id, final int version, final BigDecimal balance, final Jwt jwt) {
    log.debug("processTransaction: id={}, version={}, balance={}", id, version, balance);
    final var accountDb = accountReadService.findById(id, jwt);
    final var customerId = accountDb.getCustomerId();
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var customer = accountReadService.findCustomerById(customerId, accessToken);
    validateVersion(version, accountDb);
    validation.validateCustomerRole(customer.username(), jwt);
    final var updatedBalance = adjustBalance(balance, accountDb);
    log.trace("processTransaction: updatedBalance={}", updatedBalance);
    return accountDb;
  }

  private Account adjustBalance(final BigDecimal balance, final Account account) {
    log.debug("adjustBalance: balance={}", balance);
    final var currentBalance = account.getBalance();
    final var newBalance = currentBalance.add(balance);
    log.debug("adjustBalance: newBalance={}", newBalance);
    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
      throw new InsufficientFundsException();
    }
    account.setBalance(newBalance);
    accountRepository.save(account);
    log.debug("adjustBalance: account={}", account);
    return account;
  }

  public void deleteById(final UUID id, final int version, final Jwt jwt) {
    log.debug("deleteById: id={}, version={}", id, version);
    final var accountDb = accountReadService.findById(id, jwt);
    validation.validateRole(jwt);
    validateVersion(version, accountDb);
    accountRepository.delete(accountDb);
  }

  public void close(final UUID accountId, final int version, final Jwt jwt) {
    log.debug("close: accountId={}, versionInt={}", accountId, version);
    final var account = accountRepository.findById(accountId).orElseThrow(NotFoundException::new);
    final var token = String.format("Bearer %s", jwt.getTokenValue());
    final var customer = accountReadService.findCustomerById(account.getCustomerId(), token);
    validation.validateCustomerRole(customer, jwt);
    validateVersion(version, account);
    account.setState(CLOSED);
    log.debug("close: account={}", account);
  }

  @KafkaListener(topics = "adjustBalance",groupId = "gentlecorp")
  public void handleBalanceAdjustment(BalanceDTO2 balanceDTO) {
    log.info("handleBalanceAdjustment: balanceDTO{}", balanceDTO);
    final var account = accountRepository.findById(balanceDTO.id()).orElseThrow(NotFoundException::new);
    final var newAccount = adjustBalance(balanceDTO.amount(), account);
    log.debug("handleBalanceAdjustment: newAccount={}", newAccount);
  }
}
