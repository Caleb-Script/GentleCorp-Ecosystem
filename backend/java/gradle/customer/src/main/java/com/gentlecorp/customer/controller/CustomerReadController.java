package com.gentlecorp.customer.controller;

import com.gentlecorp.customer.model.CustomerModel;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.service.CustomerReadService;
import com.gentlecorp.customer.service.JwtService;
import com.gentlecorp.customer.util.UriHelper;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static com.gentlecorp.customer.util.Constants.ID_PATTERN;
import static com.gentlecorp.customer.util.VersionUtils.getVersion;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping(CUSTOMER_PATH)
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v2"))
@RequiredArgsConstructor
@Slf4j
public class CustomerReadController {

  private final CustomerReadService customerReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

  @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  @Operation(summary = "Search for a customer by ID", tags = "Search")
  @ApiResponse(responseCode = "200", description = "Customer found")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  public ResponseEntity<CustomerModel> getById(
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
      log.error("Despite Spring Security, getById() was called without a username in the JWT");
      return status(UNAUTHORIZED).build();
    }

    final var customer = customerReadService.findById(id, username, role, false);
    final var currentVersion = String.format("\"%s\"", customer.getVersion());

    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }

    final var model = customerToModel(customer, request);
    log.debug("getById: model={}", model);
    return ok().eTag(currentVersion).body(model);
  }

  private CustomerModel customerToModel(final Customer customer, final HttpServletRequest request) {
    final var model = new CustomerModel(customer);
    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var idUri = String.format("%s/%s", baseUri, customer.getId());

    final var selfLink = Link.of(idUri);
    final var listLink = Link.of(baseUri, LinkRelation.of("list"));
    final var addLink = Link.of(baseUri, LinkRelation.of("add"));
    final var updateLink = Link.of(idUri, LinkRelation.of("update"));
    final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
    model.add(selfLink, listLink, addLink, updateLink, removeLink);
    return model;
  }

  @GetMapping(produces = HAL_JSON_VALUE)
  @Operation(summary = "Search for customers using criteria", tags = "Search")
  @ApiResponse(responseCode = "200", description = "CollectionModel with customers found")
  @ApiResponse(responseCode = "404", description = "No customers found")
  public CollectionModel<CustomerModel> get(
    @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
    final HttpServletRequest request
  ) {
    log.debug("get: searchCriteria={}", searchCriteria);

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var models = customerReadService.find(searchCriteria)
      .stream()
      .map(customer -> {
        final var model = new CustomerModel(customer);
        model.add(Link.of(String.format("%s/%s", baseUri, customer.getId())));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return CollectionModel.of(models);
  }

  @GetMapping(path = "{id:" + ID_PATTERN + "}/all", produces = HAL_JSON_VALUE)
  public ResponseEntity<Customer> getByIdAll(
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
    log.debug("getById: role={}", role);
    final var customer = customerReadService.findById(id, username, role, true);
    final var currentVersion = String.format("\"%s\"", customer.getVersion());

    if (Objects.equals(version.orElse(null), currentVersion)) {
      return status(NOT_MODIFIED).build();
    }

    return ok().eTag(currentVersion).body(customer);
  }

}
