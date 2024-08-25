package com.gentle.bank.customer.exception;

import com.gentle.bank.customer.entity.enums.ProblemType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;

import static com.gentle.bank.customer.util.Constants.PROBLEM_PATH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * A global exception handler for managing specific exceptions and providing consistent error responses.
 * <p>
 * This class uses {@code @ControllerAdvice} to handle exceptions across the entire application. It processes
 * specific exceptions, such as {@link NotFoundException}, {@link AccessForbiddenException}, and {@link IllegalArgumentException},
 * and converts them into standardized {@link ProblemDetail} responses.
 * </p>
 *
 * <p>Exception handling methods include:</p>
 * <ul>
 *   <li>{@link #onNotFoundException(NotFoundException, HttpServletRequest)} - Handles {@link NotFoundException} and returns
 *       a {@link ProblemDetail} with HTTP status {@code 404 Not Found}.</li>
 *   <li>{@link #onAccessForbiddenException(AccessForbiddenException, HttpServletRequest)} - Handles {@link AccessForbiddenException} and
 *       returns a {@link ProblemDetail} with HTTP status {@code 403 Forbidden}.</li>
 *   <li>{@link #onIllegalArgumentException(IllegalArgumentException, HttpServletRequest)} - Handles {@link IllegalArgumentException} and
 *       returns a {@link ProblemDetail} with HTTP status {@code 400 Bad Request}.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {

  /**
   * Handles {@link NotFoundException} and creates a {@link ProblemDetail} response with HTTP status {@code 404 Not Found}.
   *
   * @param ex the {@link NotFoundException} that was thrown.
   * @param request the {@link HttpServletRequest} that triggered the exception.
   * @return a {@link ProblemDetail} containing the error message and problem details.
   */
  @ExceptionHandler
  @ResponseStatus(NOT_FOUND)
  ProblemDetail onNotFoundException(
    final NotFoundException ex,
    final HttpServletRequest request
  ) {
    log.error("onNotFound: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.NOT_FOUND.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles {@link AccessForbiddenException} and creates a {@link ProblemDetail} response with HTTP status {@code 403 Forbidden}.
   *
   * @param ex the {@link AccessForbiddenException} that was thrown.
   * @param request the {@link HttpServletRequest} that triggered the exception.
   * @return a {@link ProblemDetail} containing the error message and problem details.
   */
  @ExceptionHandler
  @ResponseStatus(FORBIDDEN)
  ProblemDetail onAccessForbiddenException(final AccessForbiddenException ex, final HttpServletRequest request) {
    log.error("onAccessForbiddenException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.FORBIDDEN.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles {@link IllegalArgumentException} and creates a {@link ProblemDetail} response with HTTP status {@code 400 Bad Request}.
   *
   * @param ex the {@link IllegalArgumentException} that was thrown.
   * @param request the {@link HttpServletRequest} that triggered the exception.
   * @return a {@link ProblemDetail} containing the error message and problem details.
   */
  @ExceptionHandler
  @ResponseStatus(BAD_REQUEST)
  ProblemDetail onIllegalArgumentException(final IllegalArgumentException ex, final HttpServletRequest request) {
    log.error("onIllegalArgumentException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.BAD_REQUEST.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles {@link NoResourceFoundException} and creates a {@link ProblemDetail} response with HTTP status {@code 404 Not Found}.
   *
   * @param ex the {@link NoResourceFoundException} that was thrown.
   * @param request the {@link HttpServletRequest} that triggered the exception.
   * @return a {@link ProblemDetail} containing the error message and problem details.
   */
  @ExceptionHandler
  @ResponseStatus(NOT_FOUND)
  ProblemDetail onNoResourceFoundException(final NoResourceFoundException ex, final HttpServletRequest request) {
    log.error("onNoResourceFoundException: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, ex.getMessage());
    problemDetail.setType(URI.create(String.format("%s/%s", PROBLEM_PATH, ProblemType.NOT_FOUND.getValue())));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }
}
