package com.gentlecorp.transaction.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public final class NotFoundException extends RuntimeException {

  private final UUID id;
  private final Map<String, List<String>> searchCriteria;

  public NotFoundException(final UUID id) {
    super(String.format("No Transaction found with ID: %s", id));
    this.id = id;
    this.searchCriteria = null;
  }

  public NotFoundException(final UUID id, final UUID accountID) {
    super(String.format("No Account found with ID: %s", accountID));
    this.id = id;
    this.searchCriteria = null;
  }

  public NotFoundException(final Map<String, List<String>> searchCriteria) {
    super(String.format("No customers found with these search criteria: %s", searchCriteria));
    this.id = null;
    this.searchCriteria = searchCriteria;
  }

  public NotFoundException() {
    super("No customers found.");
    this.id = null;
    this.searchCriteria = null;
  }
}
