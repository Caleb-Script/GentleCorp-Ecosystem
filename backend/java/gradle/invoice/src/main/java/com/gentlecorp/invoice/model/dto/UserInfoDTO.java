package com.gentlecorp.invoice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDTO(
  @JsonProperty("sub")
  String sub
) {
}

