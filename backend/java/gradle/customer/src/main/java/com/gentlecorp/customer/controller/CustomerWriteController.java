package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.exception.ConstraintViolationsException;
import com.gentlecorp.customer.exception.EmailExistsException;
import com.gentlecorp.customer.exception.VersionInvalidException;
import com.gentlecorp.customer.exception.VersionOutdatedException;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.dto.CustomerCreateDTO;
import com.gentlecorp.customer.model.dto.CustomerDTO;
import com.gentlecorp.customer.model.dto.PasswordDTO;
import com.gentlecorp.customer.model.enums.ProblemType;
import com.gentlecorp.customer.model.mapper.ContactMapper;
import com.gentlecorp.customer.model.mapper.CustomerMapper;
import com.gentlecorp.customer.service.CustomerWriteService;
import com.gentlecorp.customer.util.ControllerUtils;
import com.gentlecorp.customer.util.UriHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
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

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

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
  private final ControllerUtils controllerUtils;

  private <T> void validateDTO(T dto, Supplier<Collection<ConstraintViolation<T>>> validatorSupplier) {
    final var violations = validatorSupplier.get();

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);

      if (dto instanceof CustomerDTO) {
        @SuppressWarnings("unchecked")
        var customerViolations = (Collection<ConstraintViolation<CustomerDTO>>) (Collection<?>) violations;
        throw new ConstraintViolationsException(customerViolations, null);
      } else if (dto instanceof ContactDTO) {
        @SuppressWarnings("unchecked")
        var contactViolations = (Collection<ConstraintViolation<ContactDTO>>) (Collection<?>) violations;
        throw new ConstraintViolationsException(null, contactViolations);
      }
    }
  }




  private void validateCustomerDTO(CustomerDTO customerDTO) {
    validateDTO(customerDTO, () -> validator.validate(customerDTO, Default.class, CustomerDTO.OnCreate.class));
  }

  private void validateContactDTO(ContactDTO contactDTO) {
    validateDTO(contactDTO, () -> validator.validate(contactDTO, Default.class, CustomerDTO.OnCreate.class));
  }


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  @Operation(summary = "Create a new customer", tags = "Create")
  @ApiResponse(responseCode = "201", description = "Customer created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request body syntax")
  @ApiResponse(responseCode = "422", description = "Invalid values or email already exists")
  public ResponseEntity<Void> post(
    @RequestBody final CustomerCreateDTO customerCreateDTO,
    final HttpServletRequest request
  ) throws URISyntaxException {
    log.debug("POST: customerDTO={}", customerCreateDTO.customerDTO());
    final var customerDTO = customerCreateDTO.customerDTO();
    final var password = customerCreateDTO.passwordDTO().password();
    validateCustomerDTO(customerDTO);

    if (customerDTO.username() == null || password == null) {
      return badRequest().build();
    }

    final var customerInput = customerMapper.toCustomer(customerDTO);
    final var customer = customerWriteService.create(customerInput, password);
    final var location = uriHelper.createUri(request, customer.getId());
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
    validateCustomerDTO(customerDTO);
    final var customerInput = customerMapper.toCustomer(customerDTO);
    final var updatedCustomer = customerWriteService.update(customerInput, customerId, versionInt, jwt);
    final var etag = controllerUtils.createETag(updatedCustomer.getVersion());
    return noContent().eTag(etag).build();
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
    validateContactDTO(contactDTO);
    final var contact = customerWriteService.addContact(customerId, contactInput, jwt).getLast();
    final var location =  uriHelper.createUri(request, contact.getId());
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
    validateContactDTO(contactDTO);
    final var contactInput = contactMapper.toContact(contactDTO);
    final var updatedContact = customerWriteService.updateContact(customerId, contactId, versionInt, contactInput, jwt);
    final var etag = controllerUtils.createETag(updatedContact.getVersion());
    return noContent().eTag(etag).build();
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
    final var token = String.format("Bearer %s", jwt.getTokenValue());
    customerWriteService.deleteById(id, token);
    return noContent().build();
  }

//  @ExceptionHandler
//  public ResponseEntity<ProblemDetail> onConstraintViolations(
//    final ConstraintViolationsException ex,
//    final HttpServletRequest request
//  ) {
//    log.debug("onConstraintViolations: {}", ex.getMessage());
//
//    // Format the detail message with each violation on a new line
//    String formattedDetail = ex.getViolationsDTO().stream()
//      .map(violation -> String.format("Field '%s': %s", violation.getPropertyPath(), violation.getMessage()))
//      .collect(Collectors.joining("\n"));  // Using "\n" to separate each violation message
//
//    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, formattedDetail);
//    problemDetail.setInstance(URI.create(request.getRequestURI()));
//
//    return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
//  }


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
