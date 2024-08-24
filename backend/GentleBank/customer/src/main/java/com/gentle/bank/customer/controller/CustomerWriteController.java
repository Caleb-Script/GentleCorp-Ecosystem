package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.dto.CustomerCreateDTO;
import com.gentle.bank.customer.mapper.CustomerInputMapper;
import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.service.JwtService;
import com.gentle.bank.customer.dto.PasswordDTO;
import com.gentle.bank.customer.service.CustomerWriteService;
import com.gentle.bank.customer.exception.ConstraintViolationsException;
import com.gentle.bank.customer.exception.EmailExistsException;
import com.gentle.bank.customer.exception.VersionInvalidException;
import com.gentle.bank.customer.exception.VersionOutdatedException;
import com.gentle.bank.customer.service.patch.InvalidPatchOperationException;
import com.gentle.bank.customer.util.UriHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
 * Handles HTTP requests related to customer records, including creation, update, password update, and deletion.
 *
 * <p>This controller manages the lifecycle of customer records in the system, supporting operations such as:
 * <ul>
 *   <li>Creating new customers</li>
 *   <li>Updating existing customers</li>
 *   <li>Updating customer passwords</li>
 *   <li>Deleting customers by their ID</li>
 * </ul></p>
 *
 * <p>It also includes exception handling for validation errors, email conflicts, versioning issues, and invalid patch operations,
 * returning appropriate HTTP status codes and error details according to the RFC 7807 specification.</p>
 *
 * <p>Security is managed via JWT, with the authenticated user's role and username extracted from the JWT for authorization purposes.</p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
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
   * <p>Handles the creation of a new customer based on the provided data transfer object (DTO) and password. The
   * customer record is created in the system, and the response includes a Location header pointing to the newly
   * created resource.</p>
   *
   * @param customerCreateDTO The DTO containing the data and password for the new customer.
   * @param request The HttpServletRequest object used to construct the URI for the created resource.
   * @param jwt The JWT of the authenticated user, used to extract the username.
   * @return A ResponseEntity with a Location header pointing to the newly created customer.
   * @throws URISyntaxException If the URI syntax is incorrect.
   * @since 24.08.2024
   * @version 1.0
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   * @see CustomerWriteService#create(CustomerDTO, String, Jwt)
   * @see JwtService#getUsername(Jwt)
   * @see UriHelper#getBaseUri(HttpServletRequest)
   */
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a new customer", tags = "Create")
  @ApiResponse(responseCode = "201", description = "Customer created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "422", description = "Invalid values or email already exists")
  public ResponseEntity<Void> post(
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
   * Updates an existing customer's details.
   *
   * <p>Updates the details of an existing customer based on the provided customer ID and data. The method checks
   * for concurrent updates using the provided ETag version. The response includes no content if the update is successful.</p>
   *
   * @param id The ID of the customer to be updated.
   * @param customerDTO The DTO containing the new customer data.
   * @param version The expected version of the customer record to handle concurrent updates.
   * @param request The HttpServletRequest object.
   * @param jwt The JWT of the authenticated user.
   * @return A ResponseEntity with no content if the update is successful.
   * @since 24.08.2024
   * @version 1.0
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   * @see CustomerWriteService#update(CustomerDTO, UUID, int, String, String, Jwt)
   * @see JwtService#getUsername(Jwt)
   * @see JwtService#getRole(Jwt)
   */
  @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Update an existing customer", tags = "Update")
  @ApiResponse(responseCode = "204", description = "Customer updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  @ApiResponse(responseCode = "412", description = "Version conflict")
  @ApiResponse(responseCode = "422", description = "Invalid values or email already exists")
  @ApiResponse(responseCode = "428", description = "Version number missing")
  public ResponseEntity<Void> put(
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
   * Updates the password of an existing customer.
   *
   * <p>Updates the password for the customer identified by the provided ID. The method requires the new password
   * to be provided in the request body. The response includes no content if the update is successful.</p>
   *
   * @param id The ID of the customer whose password is to be updated.
   * @param passwordDTO The DTO containing the new password.
   * @param jwt The JWT of the authenticated user.
   * @return A ResponseEntity with no content if the password update is successful.
   * @since 24.08.2024
   * @version 1.0
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   * @see CustomerWriteService#updatePassword(Jwt, String)
   */
  @PutMapping(path = "{id:" + ID_PATTERN + "}/password", consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Update customer password", tags = "Password Update")
  @ApiResponse(responseCode = "204", description = "Password updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  @ApiResponse(responseCode = "422", description = "Invalid password")
  public ResponseEntity<Void> updatePassword(
    @PathVariable final UUID id,
    @RequestBody final PasswordDTO passwordDTO,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("updatePassword: id={}, passwordDTO={}", id, passwordDTO);
    customerWriteService.updatePassword(jwt, passwordDTO.password());
    return noContent().build();
  }

  /**
   * Deletes a customer record by its ID.
   *
   * <p>Deletes the customer record identified by the provided ID. If the customer does not exist, a 404 status code
   * is returned. The response includes no content if the deletion is successful.</p>
   *
   * @param id The ID of the customer to be deleted.
   * @param jwt The JWT of the authenticated user.
   * @return A ResponseEntity with no content if the deletion is successful.
   * @since 24.08.2024
   * @version 1.0
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   * @see CustomerWriteService#deleteById(UUID, Jwt)
   */
  @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
  @Operation(summary = "Delete a customer", tags = "Delete")
  @ApiResponse(responseCode = "204", description = "Customer deleted successfully")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  public ResponseEntity<Void> delete(
    @PathVariable final UUID id,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("delete: id={}", id);
    customerWriteService.deleteById(id);
    return noContent().build();
  }

  /**
   * Handles exceptions thrown by the controller methods.
   *
   * <p>Returns a ResponseEntity with a ProblemDetail object for various types of exceptions, including:
   * <ul>
   *   <li>HttpMessageNotReadableException</li>
   *   <li>ConstraintViolationsException</li>
   *   <li>EmailExistsException</li>
   *   <li>VersionOutdatedException</li>
   *   <li>VersionInvalidException</li>
   *   <li>InvalidPatchOperationException</li>
   * </ul></p>
   *
   * @param e The exception that was thrown.
   * @return A ResponseEntity containing the ProblemDetail object.
   * @since 24.08.2024
   * @version 1.0
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   */
  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    ConstraintViolationsException.class,
    EmailExistsException.class,
    VersionOutdatedException.class,
    VersionInvalidException.class,
    InvalidPatchOperationException.class
  })
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> handleException(Exception e) {
    log.error("Exception occurred: ", e);
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
  }
}
