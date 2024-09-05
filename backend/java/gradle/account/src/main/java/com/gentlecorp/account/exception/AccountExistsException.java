package com.gentlecorp.account.exception;

import com.gentlecorp.account.model.enums.AccountType;
import lombok.Getter;

@Getter
public class AccountExistsException extends RuntimeException {
private final AccountType accountType;
  public AccountExistsException(AccountType accountType) {
    super(String.format("Account Category: %s already exists", accountType));
    this.accountType = accountType;
  }
}
