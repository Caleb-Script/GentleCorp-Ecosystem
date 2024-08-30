package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.VersionInvalidException;
import com.gentlecorp.customer.exception.VersionOutdatedException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerCreateDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.dto.PasswordDTO;
import com.gentlecorp.customer.model.mapper.ContactMapper;
import com.gentlecorp.customer.model.mapper.CustomerMapper;
import com.gentlecorp.customer.service.CustomerWriteService;
import com.gentlecorp.customer.util.UriHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static com.gentlecorp.customer.util.Constants.ID_PATTERN;
import static com.gentlecorp.customer.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

@Controller
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteController {

  private final CustomerWriteService customerWriteService;
  private final Validator validator;
  private final CustomerMapper customerMapper;
  private final ContactMapper contactMapper;
  private final UriHelper uriHelper;


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a new customer", tags = "Create")
  @ApiResponse(responseCode = "201", description = "Customer created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "422", description = "Invalid values or email already exists")
  public ResponseEntity<Void> post(
    @RequestBody final CustomerCreateDTO customerCreateDTO,
    final HttpServletRequest request
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

    final var customerInput = customerMapper.toCustomer(customerDTO);
    final var customer = customerWriteService.create(customerInput, password);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), customer.getId()));
    return created(location).build();
  }

  @PutMapping(path = "{customerId:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Update an existing customer", tags = "Update")
  @ApiResponse(responseCode = "204", description = "Customer updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  @ApiResponse(responseCode = "412", description = "Version conflict")
  @ApiResponse(responseCode = "422", description = "Invalid values or email already exists")
  @ApiResponse(responseCode = "428", description = "Version number missing")
  public ResponseEntity<Void> put(
    @PathVariable final UUID customerId,
    @RequestBody final CustomerDTO customerDTO,
    @RequestHeader("If-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {

    log.debug("put: id={}, customerUpdateDTO={}", customerId, customerDTO);

    final int versionInt = getVersion(version, request);
    final var customerInput = customerMapper.toCustomer(customerDTO);
    final var updatedCustomer = customerWriteService.update(customerInput, customerId, versionInt, jwt);

    log.debug("put: updatedCustomer={}", updatedCustomer);
    return noContent().eTag(String.format("\"%d\"", updatedCustomer.getVersion())).build();
  }

  @PutMapping(path = "{customerId:" + ID_PATTERN + "}/password", consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Update customer password", tags = "Password Update")
  @ApiResponse(responseCode = "204", description = "Password updated successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  @ApiResponse(responseCode = "422", description = "Invalid password")
  public ResponseEntity<Void> updatePassword(
    @PathVariable final UUID customerId,
    @RequestBody final PasswordDTO passwordDTO,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("updatePassword: customerId={}, passwordDTO={}", customerId, passwordDTO);
    customerWriteService.updatePassword(jwt, passwordDTO.password());
    return noContent().build();
  }

  @PatchMapping(path = "{customerId:" + ID_PATTERN + "}/contact")
  public ResponseEntity<Void> createContact(
    @PathVariable final UUID customerId,
    @RequestBody final ContactDTO contactDTO,
    @AuthenticationPrincipal final Jwt jwt,
    final HttpServletRequest request
  ) throws URISyntaxException {
    log.debug("createContact: customerId={}, contactDTO={}", customerId, contactDTO);
    final var contactInput = contactMapper.toContact(contactDTO);
    final var contact = customerWriteService.addContact(customerId, contactInput, jwt).getLast();
    log.debug("createContact: contact={}", contact);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), contact.getId()));
    return created(location).build();
  }

  @PutMapping(path = "{customerId:" + ID_PATTERN + "}/contact/{contactId:" + ID_PATTERN + "}")
  public ResponseEntity<Void> updateContact(
    @PathVariable final UUID customerId,
    @PathVariable final UUID contactId,
    @RequestBody final ContactDTO contactDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt,
    @RequestHeader("If-Match") final Optional<String> version
  ) {
    log.debug("updateContact: customerId={}, contactId={}, version={}", customerId, contactId, version);
    log.debug("updateContact: contactDTO={}", contactDTO);

    final int versionInt = getVersion(version, request);
    final var contactInput = contactMapper.toContact(contactDTO);
    final var updatedContact = customerWriteService.updateContact(customerId, contactId, versionInt, contactInput, jwt);
    return noContent().eTag(String.format("\"%d\"", updatedContact.getVersion())).build();
  }

  @PatchMapping(path = "{customerId:" + ID_PATTERN + "}/contact/{contactId:" + ID_PATTERN + "}")
  public ResponseEntity<Void> removeContact(
    @PathVariable final UUID customerId,
    @PathVariable final UUID contactId,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt,
    @RequestHeader("If-Match") final Optional<String> version
  ) {
    log.debug("removeContact: customerId={}, contactId={}, version={}", customerId, contactId, version);
    final int versionInt = getVersion(version, request);
    customerWriteService.removeContact(customerId, contactId, versionInt, jwt);
    return noContent().build();
  }

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

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    ConstraintViolationsException.class,
    EmailExistsException.class,
    VersionOutdatedException.class,
    VersionInvalidException.class,
  })
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> handleException(Exception e) {
    log.error("Exception occurred: ", e);
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
  }
}
