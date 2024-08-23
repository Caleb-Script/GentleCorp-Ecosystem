package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleDTO(
  @JsonProperty("id")
  String id,

  @JsonProperty("name")
  String name
) {
}
