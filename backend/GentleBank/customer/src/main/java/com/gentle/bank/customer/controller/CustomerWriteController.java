package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.controller.mapper.CustomerInputMapper;
import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.service.CustomerReadService;
import com.gentle.bank.customer.service.CustomerWriteService;
import com.gentle.bank.customer.service.ProblemType;
import com.gentle.bank.customer.service.exception.ConstraintViolationsException;
import com.gentle.bank.customer.service.exception.EmailExistsException;
import com.gentle.bank.customer.service.exception.VersionInvalidException;
import com.gentle.bank.customer.service.exception.VersionOutdatedException;
import com.gentle.bank.customer.service.patch.CustomerPatcher;
import com.gentle.bank.customer.service.patch.InvalidPatchOperationException;
import com.gentle.bank.customer.service.patch.PatchOperation;
import com.gentle.bank.customer.util.UriHelper;
import com.google.common.base.Splitter;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.bank.customer.util.Constants.*;
import static com.gentle.bank.customer.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.*;

@Controller
@RequestMapping(CUSTOMER_PATH)
@RequiredArgsConstructor
@Slf4j
public class CustomerWriteController {
    /**
     * Basispfad für "type" innerhalb von ProblemDetail.
     */
    private final CustomerWriteService customerWriteService;
    private final CustomerReadService customerReadService;
    private final CustomerPatcher customerPatcher;
  private final Validator validator;
    private final CustomerInputMapper mapper;
    private final UriHelper uriHelper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Kunden anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Customer neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> post(
            @RequestBody @Validated({Default.class, CustomerDTO.OnCreate.class}) final CustomerDTO customerDTO,
            final HttpServletRequest request
    ) throws URISyntaxException {
        log.debug("POST: customerUserDTO={}", customerDTO);

        final var customerInput = mapper.toCustomer(customerDTO);

        final var customer = customerWriteService.create(customerInput);
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(STR."\{baseUri.toString()}/\{customer.getId()}");

        log.debug("POST: new Customer={}", customer);
        log.info("POST: new CustomerId={}", customer.getId());
        return created(location).build();
    }

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
            final HttpServletRequest request
    ) {
        log.debug("put: id={}, customerUpdateDTO={}", id, customerDTO);

        final int versionInt = getVersion(version, request);
        final var customerInput = mapper.toCustomer(customerDTO);
        final var updatedCustomer = customerWriteService.update(customerInput, id, versionInt);

        log.debug("put: updatedCustomer={}", updatedCustomer);
        return noContent().eTag(STR."\"\{updatedCustomer.getVersion()}\"").build();
    }

    /**
     * Einen vorhandenen Kunden anhand seiner ID löschen.
     *
     * @param id ID des zu löschenden Kunden.
     */
    @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Kunden anhand der ID loeschen", tags = "Loeschen")
    @ApiResponse(responseCode = "204", description = "Gelöscht")
    void deleteById(@PathVariable final UUID id) {
        log.debug("deleteById: id={}", id);
        customerWriteService.deleteById(id);
    }

    @ExceptionHandler
    ProblemDetail onConstraintViolations(
            final ConstraintViolationsException ex,
            final HttpServletRequest request
    ) {
        log.error("onConstraintViolations: {}", ex.getMessage());

        final var customerViolations = ex.getViolations()
                .stream()
                .map(violation -> {
                    final var path = violation.getPropertyPath();
                    final var msg = violation.getMessage();
                    final var annot = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();
                    return STR."\{path}: \{msg} (\{annot})";
                })
                .toList();
        log.error("onConstraintViolations: {}", customerViolations);
        // [ und ] aus dem String der Liste entfernen
        final var violationsStr = customerViolations.toString();
        final var detail = violationsStr.substring(1, violationsStr.length() - 2);

        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
        log.error("onEmailExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }



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
