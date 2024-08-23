package com.gentle.bank.customer.security;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MasterTokenDTO(
  @JsonProperty("access_token")
  String accessToken,
  @JsonProperty("expires_in")
  int expiresIn
) {
}
