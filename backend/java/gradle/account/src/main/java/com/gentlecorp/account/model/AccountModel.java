package com.gentlecorp.account.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.model.enums.AccountType;
import com.gentlecorp.account.model.enums.StatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.util.UUID;

@JsonPropertyOrder({
  "balance", "rateOfInterest", "withdrawalLimit", "category", "state", "customerUsername"
})
@Relation(collectionRelation = "accounts", itemRelation = "account")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class AccountModel extends RepresentationModel<AccountModel> {
  private final BigDecimal balance;
  private final int rateOfInterest;
  private final int withdrawalLimit;
  private final UUID customerId;
  private final AccountType category;
  private final StatusType state;
  private final String customerUsername;

  public AccountModel(final Account account) {
    this.balance = account.getBalance();
    this.rateOfInterest = account.getRateOfInterest();
    this.withdrawalLimit = account.getWithdrawalLimit();
    this.customerId = account.getCustomerId();
    this.category = account.getCategory();
    this.state = account.getState();
    this.customerUsername = account.getCustomerUsername();
  }
}
