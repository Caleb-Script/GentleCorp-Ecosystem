package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDTO(
  @JsonProperty("sub")
  String sub
) {
}
