package com.gentlecorp.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDTO(
  @JsonProperty("sub")
  String sub
) {
}

