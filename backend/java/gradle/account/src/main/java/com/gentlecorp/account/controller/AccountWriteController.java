package com.gentlecorp.account.controller;

import com.gentlecorp.account.exception.AccountExistsException;
import com.gentlecorp.account.exception.ConstraintViolationsException;
import com.gentlecorp.account.exception.EmailExistsException;
import com.gentlecorp.account.exception.InsufficientFundsException;
import com.gentlecorp.account.exception.VersionAheadException;
import com.gentlecorp.account.exception.VersionInvalidException;
import com.gentlecorp.account.exception.VersionOutdatedException;
import com.gentlecorp.account.model.dto.AccountDTO;
import com.gentlecorp.account.model.dto.BalanceDTO;
import com.gentlecorp.account.model.enums.ProblemType;
import com.gentlecorp.account.model.mapper.AccountMapper;
import com.gentlecorp.account.service.AccountWriteService;
import com.gentlecorp.account.service.JwtService;
import com.gentlecorp.account.util.UriHelper;
import com.google.common.base.Splitter;
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

import static com.gentlecorp.account.util.Constants.ACCOUNT_PATH;
import static com.gentlecorp.account.util.Constants.ID_PATTERN;
import static com.gentlecorp.account.util.Constants.PROBLEM_PATH;
import static com.gentlecorp.account.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.status;

@Controller
@RequestMapping(ACCOUNT_PATH)
@RequiredArgsConstructor
@Slf4j
public class AccountWriteController {

  private final AccountWriteService accountWriteService;
  private final Validator validator;
  private final AccountMapper accountMapper;
  private final UriHelper uriHelper;


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
    @RequestBody final AccountDTO accountDTO,
    @AuthenticationPrincipal final Jwt jwt,
    final HttpServletRequest request
  ) throws URISyntaxException {
    log.debug("POST: accountDTO={}", accountDTO);
    final var violations = validator.validate(accountDTO, Default.class, AccountDTO.OnCreate.class);

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);
      throw new ConstraintViolationsException(violations);
    }

    final var accountInput = accountMapper.toAccount(accountDTO);
    final var account = accountWriteService.create(accountInput, jwt);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), account.getId()));
    return created(location).build();
  }

  @PostMapping(path = "{accountId:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> closeAccount(
    @PathVariable final UUID accountId,
    @RequestHeader("If-Match") final Optional<String> version,
    @AuthenticationPrincipal final Jwt jwt,
    final HttpServletRequest request
  ) {
    final int versionInt = getVersion(version, request);
    accountWriteService.close(accountId, versionInt, jwt);
    return noContent().build();
  }

  @PutMapping(path = "{id:" + ID_PATTERN + "}/transaction")
  public ResponseEntity<Void> FundsManagement(
    @PathVariable final UUID id,
    @RequestBody final BalanceDTO balanceDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt,
    @RequestHeader("If-Match") final Optional<String> version
  ) {
    log.debug("updateBalance: id={}}", id);
    final int versionInt = getVersion(version, request);
    final var balance = balanceDTO.amount();
    final var updatedAccount = accountWriteService.processTransaction(id, versionInt, balance, jwt);
    return noContent().eTag(String.format("\"%d\"", updatedAccount.getVersion())).build();
  }

  @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
  public ResponseEntity<Void> delete(
    @PathVariable final UUID id,
    @AuthenticationPrincipal final Jwt jwt,
    final HttpServletRequest request,
    @RequestHeader("If-Match") final Optional<String> version
  ) {
    log.debug("delete: id={}", id);
    final int versionInt = getVersion(version, request);
    accountWriteService.deleteById(id, versionInt, jwt);
    return noContent().build();
  }

  @ExceptionHandler
  ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
    log.error("onEmailExists: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  ProblemDetail onAccountExists(final AccountExistsException ex, final HttpServletRequest request) {
    log.error("onAccountExists: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  ProblemDetail onInsufficientFunds(final InsufficientFundsException ex, final HttpServletRequest request) {
    log.error("onInsufficientFunds: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
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
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.PRECONDITION.getValue()));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  ProblemDetail onVersionAhead(
    final VersionAheadException ex,
    final HttpServletRequest request
  ) {
    log.error("onVersionAhead: {}", ex.getMessage());
    final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_FAILED, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.PRECONDITION.getValue()));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler
  ProblemDetail onVersionInvalid(
    final VersionInvalidException ex,
    final HttpServletRequest request
  ) {
    log.error("onVersionInvalid: {}", ex.getMessage());
    final var exceptionParts = Splitter.on(",").splitToList(ex.toString());
    final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_REQUIRED, ex.getMessage());
    problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.PRECONDITION.getValue()));
    problemDetail.setDetail(exceptionParts.get(4).trim().split("=")[1].replace("'", ""));
    problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
    return problemDetail;
  }

  @ExceptionHandler({
    HttpMessageNotReadableException.class,
    ConstraintViolationsException.class,
  })
  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  public ResponseEntity<ProblemDetail> handleException(Exception e) {
    log.error(String.format("Exception occurred: %s", e.getMessage()));
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    return ResponseEntity.status(BAD_REQUEST).body(problemDetail);
  }
}
