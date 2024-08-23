package com.gentle.bank.customer.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Role(
  @JsonProperty("id")
  String id,

  @JsonProperty("name")
  String name
) {
}
