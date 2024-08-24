package com.gentle.bank.customer.entity.enums;

import lombok.Getter;

/**
 * Enum for defining various problem types used in {@link org.springframework.http.ProblemDetail}.
 * <p>
 * This enum represents different types of issues that may occur within the application,
 * providing a standardized way to categorize and communicate errors.
 * </p>
 *
 * <p>Problem types include:</p>
 * <ul>
 *   <li>{@link #CONSTRAINTS} - Errors related to constraint violations.</li>
 *   <li>{@link #UNPROCESSABLE} - Errors when the request contains unprocessable entities (e.g., duplicate email).</li>
 *   <li>{@link #PRECONDITION} - Errors related to the header If-Match.</li>
 *   <li>{@link #BAD_REQUEST} - General bad request errors, such as invalid request parameters.</li>
 *   <li>{@link #FORBIDDEN} - Errors indicating that access to the requested resource is forbidden.</li>
 *   <li>{@link #NOT_FOUND} - Errors indicating that a requested resource was not found.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public enum ProblemType {
  /**
   * Error related to constraint violations.
   */
  CONSTRAINTS("constraints"),

  /**
   * Error when the request contains unprocessable entities, such as a duplicate email.
   */
  UNPROCESSABLE("unprocessable"),

  /**
   * Error related to the header If-Match.
   */
  PRECONDITION("precondition"),

  /**
   * General bad request errors, such as invalid request parameters.
   */
  BAD_REQUEST("badRequest"),

  /**
   * Error indicating that access to the requested resource is forbidden.
   */
  FORBIDDEN("forbidden"),

  /**
   * Error indicating that a requested resource was not found.
   */
  NOT_FOUND("notFound");

  private final String value;

  ProblemType(final String value) {
    this.value = value;
  }
}
