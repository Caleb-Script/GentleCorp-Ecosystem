package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.model.CustomerModel;
import com.gentle.bank.customer.service.JwtService;
import com.gentle.bank.customer.service.CustomerReadService;
import com.gentle.bank.customer.util.UriHelper;
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
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.bank.customer.util.Constants.CUSTOMER_PATH;
import static com.gentle.bank.customer.util.Constants.ID_PATTERN;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

/**
 * REST controller for handling HTTP GET requests related to customer data.
 *
 * <p>This controller provides endpoints for searching and retrieving customer information. It includes
 * methods to find a customer by their ID and to search for customers using various criteria provided as query parameters.</p>
 *
 * <p>The controller supports HATEOAS by adding hypermedia links to the responses, allowing clients to easily navigate
 * through related resources.</p>
 *
 * <p>Key functionalities:
 * <ul>
 *   <li>Get a customer by their ID, including handling ETag-based conditional requests to optimize network traffic.</li>
 *   <li>Search for customers based on various query parameters, returning a collection of matching customer models.</li>
 * </ul>
 * </p>
 *
 * <p>Annotations:
 * <ul>
 *   <li>{@code @RestController} - Marks the class as a Spring REST controller.</li>
 *   <li>{@code @RequestMapping} - Specifies the base URL path for the controller's endpoints.</li>
 *   <li>{@code @Observed} - Applies Micrometer's observation support for monitoring performance.</li>
 *   <li>{@code @OpenAPIDefinition} - Provides metadata for API documentation.</li>
 * </ul>
 * </p>
 *
 * @since 23.08.2024
 * @author Caleb Gyamfi
 * @see com.gentle.bank.customer.service.CustomerReadService
 * @see JwtService
 */
@RestController
@RequestMapping(CUSTOMER_PATH)
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v2"))
@RequiredArgsConstructor
@Slf4j
public class CustomerReadController {
  private final CustomerReadService customerReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

  /**
   * Searches for a customer by their ID provided as a path parameter.
   *
   * <p>This method retrieves a customer's details using their unique ID. It also handles conditional requests
   * using the ETag from the `If-None-Match` header to optimize network traffic by returning a `304 Not Modified`
   * status if the customer's data has not changed.</p>
   *
   * <p>Hypermedia links (HATEOAS) are added to the response to facilitate easy navigation through related resources.</p>
   *
   * @param id ID of the customer to search for
   * @param version ETag version from the `If-None-Match` header
   * @param request The HttpServletRequest object, used to create HATEOAS links.
   * @param jwt The JSON Web Token for security purposes
   * @return A response with status code 200 and the found customer model with hypermedia links, or status code 404 if not found.
   */
  @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
  @Observed(name = "get-by-id")
  @Operation(summary = "Search for a customer by ID", tags = "Search")
  @ApiResponse(responseCode = "200", description = "Customer found")
  @ApiResponse(responseCode = "404", description = "Customer not found")
  ResponseEntity<CustomerModel> getById(
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
    final var customer = customerReadService.findById(id, username, role);
    final var currentVersion = STR."\"\{customer.getVersion()}\"";

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
    final var idUri = STR."\{baseUri}/\{customer.getId()}";

    final var selfLink = Link.of(idUri);
    final var listLink = Link.of(baseUri, LinkRelation.of("list"));
    final var addLink = Link.of(baseUri, LinkRelation.of("add"));
    final var updateLink = Link.of(idUri, LinkRelation.of("update"));
    final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
    model.add(selfLink, listLink, addLink, updateLink, removeLink);
    return model;
  }

  /**
   * Searches for customers based on various search criteria provided as query parameters.
   *
   * <p>This method allows searching for multiple customers using a map of query parameters. The found customers
   * are returned as a `CollectionModel`, which includes the customers and hypermedia links for further navigation.</p>
   *
   * @param searchCriteria The query parameters as a map.
   * @param request The HttpServletRequest object, used to create HATEOAS links.
   * @return A response with status code 200 and a `CollectionModel` of found customers, or status code 404 if no customers are found.
   */
  @GetMapping(produces = HAL_JSON_VALUE)
  @Operation(summary = "Search for customers using criteria", tags = "Search")
  @ApiResponse(responseCode = "200", description = "CollectionModel with customers found")
  @ApiResponse(responseCode = "404", description = "No customers found")
  CollectionModel<CustomerModel> get(
    @RequestParam @NonNull final MultiValueMap<String, String> searchCriteria,
    final HttpServletRequest request
  ) {
    log.debug("get: searchCriteria={}", searchCriteria);

    final var baseUri = uriHelper.getBaseUri(request).toString();
    final var models = customerReadService.find(searchCriteria)
      .stream()
      .map(customer -> {
        final var model = new CustomerModel(customer);
        model.add(Link.of(STR."\{baseUri}/\{customer.getId()}"));
        return model;
      })
      .toList();

    log.debug("get: models={}", models);
    return CollectionModel.of(models);
  }
}
