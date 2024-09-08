package com.gentlecorp.invoice.model.entity;

import java.util.UUID;

public record Account(
  UUID id,
  String customerUsername
) {
}
