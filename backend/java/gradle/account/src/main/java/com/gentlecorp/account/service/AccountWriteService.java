package com.gentlecorp.account.service;

import com.gentlecorp.account.exception.NotFoundException;
import com.gentlecorp.account.exception.VersionOutdatedException;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static com.gentlecorp.account.model.enums.StatusType.ACTIVE;
import static com.gentlecorp.account.model.enums.StatusType.CLOSED;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountWriteService {

  private final AccountReadService accountReadService;
  private final AccountRepository accountRepository;

  public Account create(final Account account) {
    account.setState(ACTIVE);
    log.debug("create: account={}", account);

    final var accountDb = accountRepository.save(account);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    log.debug("create: accountDb={}", accountDb);
    return accountDb;
  }

  public Account update(final Account account, final UUID id, final int version) {
    log.debug("update: account={}", account);
    log.debug("update: id={}, version={}", id, version);
    log.trace("update: No constraints violated");

    account.setState(ACTIVE);
    final var accountDb = accountRepository.findById(id).orElseThrow(() -> new NotFoundException(id));

    if (version != accountDb.getVersion()) {
      log.error("Version is not the current version");
      throw new VersionOutdatedException(version);
    }
    return accountRepository.save(accountDb);
  }

  public Account updateBalance(final UUID id, final int version, final BigDecimal balance, final String username, final String role, final String token) {
    log.debug("updateBalance: id={}, version={}, balance={}", id, version, balance);
    final var accountDb = accountReadService.findById(id,username,role, token);
    final var currentBalance = accountDb.getBalance();
    accountDb.setBalance(currentBalance.add(balance));
    accountRepository.save(accountDb);
    return accountDb;
  }

  public void deleteById(final UUID id) {
    log.debug("deleteById: id={}", id);
    final var account = accountRepository.findById(id).orElseThrow(NotFoundException::new);
    accountRepository.delete(account);
  }

  public void close(final UUID accountId, final int versionInt) {
    log.debug("close: accountId={}, versionInt={}", accountId, versionInt);
    final var account = accountRepository.findById(accountId).orElseThrow(NotFoundException::new);

    if (versionInt != account.getVersion()) {
      log.error("Version is not the current version");
      throw new VersionOutdatedException(versionInt);
    }
    account.setState(CLOSED);
  }
}
