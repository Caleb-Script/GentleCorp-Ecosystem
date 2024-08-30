package com.gentlecorp.account.controller;

import com.gentlecorp.account.exception.ConstraintViolationsException;
import com.gentlecorp.account.exception.EmailExistsException;
import com.gentlecorp.account.exception.VersionInvalidException;
import com.gentlecorp.account.exception.VersionOutdatedException;
import com.gentlecorp.account.model.dto.AccountDTO;
import com.gentlecorp.account.model.dto.BalanceDTO;
import com.gentlecorp.account.model.mapper.AccountMapper;
import com.gentlecorp.account.service.AccountWriteService;
import com.gentlecorp.account.service.JwtService;
import com.gentlecorp.account.util.UriHelper;
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
import static com.gentlecorp.account.util.VersionUtils.getVersion;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
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
  private final JwtService jwtService;
  private final Validator validator;
  private final AccountMapper accountMapper;
  private final UriHelper uriHelper;


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
    @RequestBody final AccountDTO accountDTO,
    final HttpServletRequest request
  ) throws URISyntaxException {
    log.debug("POST: accountDTO={}", accountDTO);
    final var violations = validator.validate(accountDTO, Default.class, AccountDTO.OnCreate.class);

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);
      throw new ConstraintViolationsException(violations);
    }

    final var accountInput = accountMapper.toAccount(accountDTO);
    final var account = accountWriteService.create(accountInput);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), account.getId()));
    return created(location).build();
  }

  @PutMapping(path = "{accountId:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> put(
    @PathVariable final UUID accountId,
    @RequestBody final AccountDTO accountDTO,
    @RequestHeader("If-Match") final Optional<String> version,
    final HttpServletRequest request
  ) {

    log.debug("put: id={}, accountUpdateDTO={}", accountId, accountDTO);

    final int versionInt = getVersion(version, request);
    final var accountInput = accountMapper.toAccount(accountDTO);
    final var updatedAccount = accountWriteService.update(accountInput, accountId, versionInt);

    log.debug("put: updatedAccount={}", updatedAccount);
    return noContent().eTag(String.format("\"%d\"", updatedAccount.getVersion())).build();
  }

  @PatchMapping(path = "{id:" + ID_PATTERN + "}/balance")
  public ResponseEntity<Void> updateBalance(
    @PathVariable final UUID id,
    @RequestBody final BalanceDTO balanceDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt,
    @RequestHeader("If-Match") final Optional<String> version
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, version={}, username={}", id, version, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a username in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }
    log.debug("updateBalance: id={}}", id);
    final int versionInt = getVersion(version, request);
    final var balance = balanceDTO.newValue();
    final var token = "Bearer " + jwt.getTokenValue();
    final var updatedAccount = accountWriteService.updateBalance(id, versionInt, balance, username, role, token);
    return noContent().eTag(String.format("\"%d\"", updatedAccount.getVersion())).build();
  }

  @DeleteMapping(path = "{id:" + ID_PATTERN + "}")
  public ResponseEntity<Void> delete(
    @PathVariable final UUID id,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("delete: id={}", id);
    accountWriteService.deleteById(id);
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
