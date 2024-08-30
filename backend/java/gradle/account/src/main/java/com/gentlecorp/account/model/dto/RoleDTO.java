package com.gentlecorp.account.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleDTO(
  @JsonProperty("id")
  String id,

  @JsonProperty("lastName")
  String name
) {
}
