package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.dto.CustomerCreateDTO;
import com.gentle.bank.customer.mapper.CustomerInputMapper;
import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.service.JwtService;
import com.gentle.bank.customer.dto.PasswordDTO;
import com.gentle.bank.customer.service.CustomerWriteService;
import com.gentle.bank.customer.service.ProblemType;
import com.gentle.bank.customer.exception.ConstraintViolationsException;
import com.gentle.bank.customer.exception.EmailExistsException;
import com.gentle.bank.customer.exception.VersionInvalidException;
import com.gentle.bank.customer.exception.VersionOutdatedException;
import com.gentle.bank.customer.service.patch.InvalidPatchOperationException;
import com.gentle.bank.customer.util.UriHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.bank.customer.util.Constants.*;
import static com.gentle.bank.customer.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

/**
 * This controller handles the creation, update, and deletion of customer records in the system.
 * It includes methods for creating a new customer, updating an existing customer, updating the customer's password,
 * and deleting a customer by their ID.
 * <p>
 * The controller also handles exceptions related to validation, existing email conflicts, versioning, and invalid patch operations.
 * Each method and exception handler returns appropriate HTTP status codes and error details as per the RFC 7807 specification.
 * </p>
 * <p>
 * The API endpoints are secured using JWT, and the customer's role and username are extracted from the JWT for authorization purposes.
 * </p>
 *
 * @since 23.08.2024
 * @author Caleb Gyamfi
 * @see com.gentle.bank.customer.service.CustomerWriteService
 * @see com.gentle.bank.customer.service.CustomerReadService
 * @see com.gentle.bank.customer.service.patch.CustomerPatcher
 * @see com.gentle.bank.customer.dto.CustomerDTO
 * @see JwtService
 */
@Controller
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteController {

  private final CustomerWriteService customerWriteService;
  private final Validator validator;
  private final CustomerInputMapper mapper;
  private final UriHelper uriHelper;
  private final JwtService jwtService;

  /**
   * Creates a new customer in the system.
   *
   * @param customerCreateDTO The DTO containing customer data and password for the new customer.
   * @param request The HttpServletRequest object, used to create the URI for the created resource.
   * @param jwt The JWT of the authenticated user, used to extract the username.
   * @return A ResponseEntity with a Location header pointing to the newly created customer.
   * @throws URISyntaxException If the URI syntax is incorrect.
   */
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Einen neuen Kunden anlegen", tags = "Neuanlegen")
  @ApiResponse(responseCode = "201", description = "Customer neu angelegt")
  @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
  @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
  ResponseEntity<Void> post(
    @RequestBody final CustomerCreateDTO customerCreateDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) throws URISyntaxException {
    final var customerDTO = customerCreateDTO.customerDTO();
    final var password = customerCreateDTO.passwordDTO().password();
    log.debug("POST: customerDTO={}", customerDTO);
    final var violations = validator.validate(customerDTO, Default.class, CustomerDTO.OnCreate.class);

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);
      throw new ConstraintViolationsException(violations);
    }

    if (customerDTO.username() == null || password == null) {
      return badRequest().build();
    }

    final var customerInput = mapper.toCustomer(customerDTO);
    final var customer = customerWriteService.create(customerInput, password, jwt);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), customer.getId()));
    return created(location).build();
  }

  /**
   * Updates an existing customer's details based on the provided customer ID.
   *
   * @param id The ID of the customer to be updated.
   * @param customerDTO The DTO containing the new customer data.
   * @param version The expected version of the customer record, to ensure concurrent updates are handled properly.
   * @param request The HttpServletRequest object.
   * @param jwt The JWT of the authenticated user.
   * @return A ResponseEntity with no content if the update is successful.
   */
  @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Einen Kunden mit neuen Werten aktualisieren", tags = "Aktualisieren")
  @ApiResponse(responseCode = "204", description = "Aktualisiert")
  @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
  @ApiResponse(responseCode = "404", description = "Customer nicht vorhanden")
  @ApiResponse(responseCode = "412", description = "Versionsnummer falsch")
  @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
  @ApiResponse(responseCode = "428", description = VERSION_NUMBER_MISSING)
  ResponseEntity<Void> put(
    @PathVariable final UUID id,
    @RequestBody final CustomerDTO customerDTO,
    @RequestHeader("If-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("put: id={}, username={}, customerUpdateDTO={}", id, username, customerDTO);

    final var role = jwtService.getRole(jwt);
    log.trace("getById: role={}", role);

    final int versionInt = getVersion(version, request);
    final var customerInput = mapper.toCustomer(customerDTO);
    final var updatedCustomer = customerWriteService.update(customerInput, id, versionInt, role, username, jwt);

    log.debug("put: updatedCustomer={}", updatedCustomer);
    return noContent().eTag(String.format("\"%d\"", updatedCustomer.getVersion())).build();
  }

  /**
   * Updates the password of an existing customer based on the provided customer ID.
   *
   * @param id The ID of the customer whose password is to be updated.
   * @param passwordDTO The DTO containing the new password.
   * @param jwt The JWT of the authenticated user.
   * @return A ResponseEntity with no content if the password update is successful.
   */
  @PutMapping(path = "{id:" + ID_PATTERN + "}/password", consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<Void> updatePassword(
    @PathVariable final UUID id,
    @RequestBody @Valid final PasswordDTO passwordDTO,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("updatePassword: id={} pass={}", id, passwordDTO.password());

    customerWriteService.updatePassword(jwt, passwordDTO.password());
    return noContent().build();
  }

  /**
   * Deletes an existing customer based on the provided customer ID.
   *
   * @param id The ID of the customer to be deleted.
   */
  @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
  @ResponseStatus(NO_CONTENT)
  @Operation(summary = "Einen Kunden anhand der ID loeschen", tags = "Loeschen")
  @ApiResponse(responseCode = "204", description = "Gelöscht")
  void deleteById(@PathVariable final UUID id) {
    log.debug("deleteById: id={}", id);
    customerWriteService.deleteById(id);
  }

  /**
   * Handles validation constraint violations by returning a ProblemDetail with status 422.
   *
   * @param ex The exception containing the validation errors.
   * @param request The HttpServletRequest object.
   * @return A ProblemDetail object with detailed validation errors.
   */
  @ExceptionHandler
  ProblemDetail onConstraintViolations(
    final ConstraintViolationsException ex,
    final HttpServletRequest request
  ) {
    log.error("onConstraintViolations: {}", ex.getMessage());

    final var customerViolations = ex.getViolationsDTO()
      .stream()
      .map(violation -> {
        final var path = violation.getPropertyPath();
        final var msg = violation.getMessage();
        final var annot = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
        return STR."\{path}: \{msg} (\{annot})";
      })
      .toList();
    log.error("onConstraintViolations: {}", customerViolations);
    final var violationsStr = customerViolations.toString();
    final var detail = violationsStr.substring(1, violationsStr.length() - 2);

    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

    return problemDetail;
  }

  /**
   * Handles email conflicts by returning a ProblemDetail with status 422.
   *
   * @param ex The exception thrown when an email address already exists in the system.
   * @return A ProblemDetail object with an error message indicating the email conflict.
   */
  @ExceptionHandler
  ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
    log.error("onEmailExists: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles version conflicts by returning a ProblemDetail with status 412.
   *
   * @param ex The exception thrown when the version of the customer record is outdated.
   * @return A ProblemDetail object with an error message indicating the version conflict.
   */
  @ExceptionHandler
  ProblemDetail onVersionOutdated(
    final VersionOutdatedException ex,
    final HttpServletRequest request
  ) {
    log.error("onVersionOutdated: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_FAILED, ex.getMessage());
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles invalid version numbers by returning a ProblemDetail with status 428.
   *
   * @param ex The exception thrown when the version number is invalid.
   * @return A ProblemDetail object with an error message indicating the invalid version number.
   */
  @ExceptionHandler
  ProblemDetail onVersionInvalidException(
    final VersionInvalidException ex,
    final HttpServletRequest request
  ) {
    log.error("onVersionInvalidException: {}", ex.getMessage());
    final var detail = ex.getMessage().split(",")[4].split("=")[1];
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles invalid patch operations by returning a ProblemDetail with status 422.
   *
   * @param ex The exception thrown when an invalid patch operation is performed.
   * @return A ProblemDetail object with an error message indicating the invalid patch operation.
   */
  @ExceptionHandler
  ProblemDetail onInvalidPatchOperation(
    final InvalidPatchOperationException ex,
    final HttpServletRequest request
  ) {
    log.error("onMessageNotReadable: {}", ex.getMessage());
    final var detail = ex.getMessage().split(",")[4].split("=")[1];
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.UNPROCESSABLE.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  /**
   * Handles invalid JSON inputs by returning a ProblemDetail with status 400.
   *
   * @param ex The exception thrown when the JSON request body is not readable or invalid.
   * @return A ProblemDetail object with an error message indicating the invalid JSON input.
   */
  @ExceptionHandler
  ProblemDetail onMessageNotReadable(
    final HttpMessageNotReadableException ex,
    final HttpServletRequest request
  ) {
    log.error("onMessageNotReadable: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
    problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.BAD_REQUEST.getValue()}"));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }
}

