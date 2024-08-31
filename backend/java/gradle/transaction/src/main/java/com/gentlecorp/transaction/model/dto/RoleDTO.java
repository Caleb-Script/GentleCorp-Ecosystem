package com.gentlecorp.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleDTO(
  @JsonProperty("id")
  String id,

  @JsonProperty("lastName")
  String name
) {
}
