package com.gentlecorp.transaction.controller;

import com.gentlecorp.transaction.model.TransactionModel;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.service.JwtService;
import com.gentlecorp.transaction.service.TransactionReadService;
import com.gentlecorp.transaction.util.UriHelper;
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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.transaction.util.Constants.TRANSACTION_PATH;
import static com.gentlecorp.transaction.util.Constants.ID_PATTERN;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(TRANSACTION_PATH)
@RequiredArgsConstructor
@Slf4j
public class TransactionReadController {

  private final TransactionReadService transactionReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

  @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  public ResponseEntity<TransactionModel> getTransactionById(
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
    final var transaction = transactionReadService.findById(id, username, role, token);
    final var currentVersion = String.format("\"%s\"", transaction.getVersion());

    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }

    final var model = transactionToModel(transaction, request);
    log.debug("getById: model={}", model);
    return ok().eTag(currentVersion).body(model);
  }

  private TransactionModel transactionToModel(final Transaction transaction, final HttpServletRequest request) {
    final var model = new TransactionModel(transaction);
    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var idUri = String.format("%s/%s", baseUri, transaction.getId());

    final var selfLink = Link.of(idUri);
    final var listLink = Link.of(baseUri, LinkRelation.of("list"));
    final var addLink = Link.of(baseUri, LinkRelation.of("add"));
    final var updateLink = Link.of(idUri, LinkRelation.of("update"));
    final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
    model.add(selfLink, listLink, addLink, updateLink, removeLink);
    return model;
  }

  @GetMapping(produces = HAL_JSON_VALUE)
  public CollectionModel<TransactionModel> get(
    @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("get: searchCriteria={}", searchCriteria);

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var token = "Bearer " + jwt.getTokenValue();
    final var models = transactionReadService.find(searchCriteria, token)
      .stream()
      .map(transaction -> {
        final var model = new TransactionModel(transaction);
        model.add(Link.of(String.format("%s/%s", baseUri, transaction.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return CollectionModel.of(models);
  }

  @GetMapping(path = "all/{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<TransactionModel>> getByAccountId(
    @PathVariable final UUID id,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, username={}", id, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a username in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var token = "Bearer " + jwt.getTokenValue();
    final var models = transactionReadService.findByAccountId(id,username, token)
      .stream()
      .map(transaction -> {
        final var model = new TransactionModel(transaction);
        model.add(Link.of(String.format("%s/%s", baseUri, transaction.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return ok().body(CollectionModel.of(models));
  }
}
