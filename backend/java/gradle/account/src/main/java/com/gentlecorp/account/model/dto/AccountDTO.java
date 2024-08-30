package com.gentlecorp.account.model.dto;

import com.gentlecorp.account.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountDTO(
  BigDecimal balance,
  AccountType category,
  int rateOfInterest,
  int overdraft,
  int withdrawalLimit,
  UUID customerId
) {
  public interface OnCreate { }
}
