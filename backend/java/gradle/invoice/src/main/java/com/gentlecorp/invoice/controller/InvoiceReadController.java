package com.gentlecorp.invoice.controller;

import com.gentlecorp.invoice.model.InvoiceModel;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.service.InvoiceReadService;
import com.gentlecorp.invoice.service.JwtService;
import com.gentlecorp.invoice.util.UriHelper;
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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.invoice.util.Constants.ID_PATTERN;
import static com.gentlecorp.invoice.util.Constants.INVOICE_PATH;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(INVOICE_PATH)
@RequiredArgsConstructor
@Slf4j
public class InvoiceReadController {

  private final InvoiceReadService invoiceReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

  @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  public ResponseEntity<InvoiceModel> getInvoiceById(
    @PathVariable final UUID id,
    @RequestHeader("If-None-Match") final Optional<String> version,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, version={}, customerUsername={}", id, version, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a customerUsername in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var token = "Bearer " + jwt.getTokenValue();
    final var invoice = invoiceReadService.findById(id, username, role, token);
    final var currentVersion = String.format("\"%s\"", invoice.getVersion());

    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }

    final var model = invoiceToModel(invoice, request);
    log.debug("getById: model={}", model);
    return ok().eTag(currentVersion).body(model);
  }

  private InvoiceModel invoiceToModel(final Invoice invoice, final HttpServletRequest request) {
    final var model = new InvoiceModel(invoice);
    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var idUri = String.format("%s/%s", baseUri, invoice.getId());

    final var selfLink = Link.of(idUri);
    final var listLink = Link.of(baseUri, LinkRelation.of("list"));
    final var addLink = Link.of(baseUri, LinkRelation.of("add"));
    final var updateLink = Link.of(idUri, LinkRelation.of("update"));
    final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
    model.add(selfLink, listLink, addLink, updateLink, removeLink);
    return model;
  }

  @GetMapping(produces = HAL_JSON_VALUE)
  public CollectionModel<InvoiceModel> get(
    @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    log.debug("get: searchCriteria={}", searchCriteria);

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var token = "Bearer " + jwt.getTokenValue();
    final var models = invoiceReadService.find(searchCriteria, token)
      .stream()
      .map(invoice -> {
        final var model = new InvoiceModel(invoice);
        model.add(Link.of(String.format("%s/%s", baseUri, invoice.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return CollectionModel.of(models);
  }

  @GetMapping(path = "all/{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  public ResponseEntity<CollectionModel<InvoiceModel>> getByAccountId(
    @PathVariable final UUID id,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, customerUsername={}", id, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a customerUsername in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var token = "Bearer " + jwt.getTokenValue();
    final var models = invoiceReadService.findByAccountId(id,role, username, token)
      .stream()
      .map(invoice -> {
        final var model = new InvoiceModel(invoice);
        model.add(Link.of(String.format("%s/%s", baseUri, invoice.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return ok().body(CollectionModel.of(models));
  }

  @GetMapping(path = "{invoiceId:" + ID_PATTERN + "}/payments", produces = HAL_JSON_VALUE)
  public ResponseEntity<Collection<Payment>> getPaymentsByInvoice(
    @PathVariable final UUID invoiceId,
    @AuthenticationPrincipal final Jwt jwt
  ) {
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: id={}, customerUsername={}", invoiceId, username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a customerUsername in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var token = "Bearer " + jwt.getTokenValue();
    final var invoice = invoiceReadService.findById(invoiceId,role, username, token);
    final var payments = invoice.getPayments();

    return ok().body(payments);
  }
}
