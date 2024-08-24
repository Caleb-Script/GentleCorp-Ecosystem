package com.gentle.bank.customer.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static com.gentle.bank.customer.entity.enums.ProblemType.PRECONDITION;
import static com.gentle.bank.customer.util.Constants.PROBLEM_PATH;

/**
 * Exception thrown when a request includes an invalid or unsupported version.
 * <p>
 * This exception extends {@link ErrorResponseException} and provides detailed
 * information about the problem using {@link ProblemDetail}. It is used to signal
 * issues related to versioning constraints in the application.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public class VersionInvalidException extends ErrorResponseException {

  /**
   * Constructs a new {@code VersionInvalidException} with the specified status, message, and URI.
   *
   * @param status The HTTP status code representing the error.
   * @param message The detail message explaining the error.
   * @param uri The URI associated with the error.
   */
  public VersionInvalidException(final HttpStatusCode status, final String message, final URI uri) {
    this(status, message, uri, null);
  }

  /**
   * Constructs a new {@code VersionInvalidException} with the specified status, message, URI, and cause.
   *
   * @param status The HTTP status code representing the error.
   * @param message The detail message explaining the error.
   * @param uri The URI associated with the error.
   * @param cause The cause of the error (can be {@code null}).
   */
  public VersionInvalidException(
    final HttpStatusCode status,
    final String message,
    final URI uri,
    final Throwable cause
  ) {
    super(status, asProblemDetail(status, message, uri), cause);
  }

  /**
   * Converts the given status, message, and URI into a {@link ProblemDetail} object.
   *
   * @param status The HTTP status code representing the error.
   * @param detail The detail message explaining the error.
   * @param uri The URI associated with the error.
   * @return A {@link ProblemDetail} object representing the error.
   */
  private static ProblemDetail asProblemDetail(final HttpStatusCode status, final String detail, final URI uri) {
    final var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setType(URI.create(PROBLEM_PATH + PRECONDITION.getValue()));
    problemDetail.setInstance(uri);
    return problemDetail;
  }
}
