package com.gentlecorp.invoice.model.enums;

import lombok.Getter;

@Getter
public enum ProblemType {
  CONSTRAINTS("constraints"),
  UNPROCESSABLE("unprocessable"),
  PRECONDITION("precondition"),
  BAD_REQUEST("badRequest"),
  FORBIDDEN("forbidden"),
  NOT_FOUND("notFound");

  private final String value;

  ProblemType(final String value) {
    this.value = value;
  }
}
