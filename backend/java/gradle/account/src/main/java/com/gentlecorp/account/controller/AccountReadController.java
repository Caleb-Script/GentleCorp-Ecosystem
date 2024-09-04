package com.gentlecorp.account.controller;

import com.gentlecorp.account.model.AccountModel;
import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.service.AccountReadService;
import com.gentlecorp.account.service.JwtService;
import com.gentlecorp.account.util.UriHelper;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.account.util.Constants.ACCOUNT_PATH;
import static com.gentlecorp.account.util.Constants.ID_PATTERN;
import static com.gentlecorp.account.util.VersionUtils.getVersion;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(ACCOUNT_PATH)
@RequiredArgsConstructor
@Slf4j
public class AccountReadController {

  private final AccountReadService accountReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

  @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  public ResponseEntity<AccountModel> getAccountById(
    @PathVariable final UUID id,
    @RequestHeader("If-None-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
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
    final var token = "Bearer " + jwt.getTokenValue();
    final var account = accountReadService.findById(id, username, role, token);
    final var currentVersion = String.format("\"%s\"", account.getVersion());

    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }

    final var model = accountToModel(account, request);
    log.debug("getById: model={}", model);
    return ok().eTag(currentVersion).body(model);
  }

  @GetMapping(path = "customer/{customerId:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  public ResponseEntity<Collection<AccountModel>> getAccountByCustomer(
    @PathVariable final UUID customerId,
    @RequestHeader("If-None-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, version={}, username={}", customerId, version, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a username in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var token = "Bearer " + jwt.getTokenValue();
    final var accountList = accountReadService.findByCustomerId(customerId, token);


    final var modelList = accountList.stream()
        .map(account -> accountToModel(account, request))
          .toList();

    return ok().body(modelList);
  }

  private AccountModel accountToModel(final Account account, final HttpServletRequest request) {
    final var model = new AccountModel(account);
    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var idUri = String.format("%s/%s", baseUri, account.getId());

    final var selfLink = Link.of(idUri);
    final var listLink = Link.of(baseUri, LinkRelation.of("list"));
    final var addLink = Link.of(baseUri, LinkRelation.of("add"));
    final var updateLink = Link.of(idUri, LinkRelation.of("update"));
    final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
    model.add(selfLink, listLink, addLink, updateLink, removeLink);
    return model;
  }

  @GetMapping(produces = HAL_JSON_VALUE)
  public CollectionModel<AccountModel> get(
    @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("get: searchCriteria={}", searchCriteria);

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var token = "Bearer " + jwt.getTokenValue();
    final var models = accountReadService.find(searchCriteria, token)
      .stream()
      .map(account -> {
        final var model = new AccountModel(account);
        model.add(Link.of(String.format("%s/%s", baseUri, account.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return CollectionModel.of(models);
  }

  @GetMapping(path = "/{id:" + ID_PATTERN + "}/balance", produces = HAL_JSON_VALUE)
  public ResponseEntity<BigDecimal> getBalanceById(
    @PathVariable final UUID id,
    @RequestHeader("If-None-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
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
    final int versionInt = getVersion(version, request);
    final var token = "Bearer " + jwt.getTokenValue();
    final var account = accountReadService.findById(id, username, role, token);

    final var currentVersion = String.format("\"%s\"", account.getVersion());
    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }
    final var balance = account.getBalance();
    return ok().body(balance);
  }

  @GetMapping(path = "/{id:" + ID_PATTERN + "}/total", produces = HAL_JSON_VALUE)
  public ResponseEntity<BigDecimal> getTotalBalance(
    @PathVariable final UUID id,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("getBalanceFromAccountById: accountId={}", id);
    final var token = "Bearer " + jwt.getTokenValue();
    final var balance = accountReadService.getFullBalance(id, token);
    return ok().body(balance);
  }
}
