package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.controller.model.CustomerModel;
import com.gentle.bank.customer.security.JwtService;
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
 * Eine Controller-Klasse bildet die REST-Schnittstelle, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Methoden der Klasse abgebildet werden. Public, damit Pfade für Zugriffsschutz verwendet werden können.
 * <img src="../../../../../asciidoc/CustomerGetController.svg" alt="Klassendiagramm">
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@RestController
@RequestMapping(CUSTOMER_PATH)
@OpenAPIDefinition(info = @Info(title = "Customer API", version = "v2"))
@RequiredArgsConstructor
@Slf4j
public class CustomerGetController {
  private final CustomerReadService customerReadService;
  private final JwtService jwtService;
  private final UriHelper uriHelper;

    /**
     * Suche anhand der Customer-ID als Pfad-Parameter.
     *
     * @param id ID des zu suchenden Kunden
     * @param version Versionsnummer aus dem Header If-None-Match
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @param jwt JWT für Security
     * @return Ein Response mit dem Statuscode 200 und dem gefundenen Kunden mit Atom-Links oder Statuscode 404.
     */
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Observed(name = "get-by-id")
    @Operation(summary = "Suche mit der Customer-ID", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Customer gefunden")
    @ApiResponse(responseCode = "404", description = "Customer nicht gefunden")
    ResponseEntity<CustomerModel> getById(
        @PathVariable final UUID id,
        @RequestHeader("If-None-Match") final Optional<String> version,
        final HttpServletRequest request,
        @AuthenticationPrincipal final Jwt jwt
    ) {
      final var username = jwtService.getUsername(jwt);
        log.debug("getById: id={}, version={}, username={}", id, version, username);

      if (username == null) {
        log.error("Trotz Spring Security wurde getById() ohne Benutzername im JWT aufgerufen");
        return status(UNAUTHORIZED).build();
      }

      final var role = jwtService.getRole(jwt);
      log.trace("getById: role={}", role);

        final var customer = customerReadService.findById(id, username, role);
        log.debug("getById: {}", customer);

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
     * Suche mit diversen Suchkriterien als Query-Parameter.
     *
     * @param searchCriteria Query-Parameter als Map.
     * @param request Das Request-Objekt, um Links für HATEOAS zu erstellen.
     * @return Ein Response mit dem Statuscode 200 und den gefundenen Kunden als CollectionModel oder Statuscode 404.
     */
    @GetMapping(produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit Suchkriterien", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "CollectionModel mid den Kunden")
    @ApiResponse(responseCode = "404", description = "Keine Kunden gefunden")
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
