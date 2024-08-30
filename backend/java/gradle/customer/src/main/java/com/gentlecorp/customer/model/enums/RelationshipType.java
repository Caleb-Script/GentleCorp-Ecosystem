package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum RelationshipType {
  PARTNER("Partner"),
  BUSINESS_PARTNER("Business Partner"),
  RELATIVE("Relative"),
  COLLEAGUE("Colleague"),
  PARENT("Parent"),
  SIBLING("Sibling"),
  CHILD("Child");

  private final String relationship;

  @JsonCreator
  public static RelationshipType of(final String value) {
    return Stream.of(values())
      .filter(relationshipType -> relationshipType.relationship.equalsIgnoreCase(value))
      .findFirst()
      .orElse(null);
  }
}
