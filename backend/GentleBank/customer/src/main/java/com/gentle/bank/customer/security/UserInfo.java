package com.gentle.bank.customer.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfo(
  @JsonProperty("sub")
  String sub
) {
}
