package com.gentle.bank.customer.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.gentle.bank.customer.util.Constants.CUSTOMER_PATH;

/**
 * Utility class for handling URI generation based on HTTP request information.
 * <p>
 * The {@code UriHelper} class provides methods to generate a base URI for the application, considering
 * different scenarios such as requests coming through an API Gateway or direct requests.
 * </p>
 * <p>
 * The base URI is generated either by using forwarded headers (when available) or directly from the
 * request URI components. This class handles both scenarios:
 * <ul>
 *   <li>When forwarding headers are present (e.g., through Kubernetes Ingress Controller or Spring Cloud Gateway).</li>
 *   <li>When no forwarding headers are present (i.e., direct request).</li>
 * </ul>
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Component
@Slf4j
public class UriHelper {
  private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
  private static final String X_FORWARDED_HOST = "x-forwarded-host";
  private static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
  private static final String CUSTOMER_PREFIX = "/customer";

  /**
   * Determines the base URI of the request, excluding query parameters.
   * <p>
   * This method constructs the base URI based on whether the request includes forwarding headers or not.
   * If forwarding headers are present, it uses those to build the base URI. Otherwise, it builds the URI
   * directly from the request's scheme, host, port, and a fixed customer path.
   * </p>
   *
   * @param request The HTTP request from which the base URI is derived.
   * @return The base URI as a {@link URI} object.
   */
  public URI getBaseUri(final HttpServletRequest request) {
    final var forwardedHost = request.getHeader(X_FORWARDED_HOST);
    if (forwardedHost != null) {
      // Forwarding through Kubernetes Ingress Controller or Spring Cloud Gateway
      return getBaseUriForwarded(request, forwardedHost);
    }

    // No forwarding from an API Gateway
    // URI from scheme, host, port, and path
    final var uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
    final var baseUri =
      STR."\{uriComponents.getScheme()}://\{uriComponents.getHost()}:\{uriComponents.getPort()}\{CUSTOMER_PATH}";
    log.debug("getBaseUri (without forwarding): baseUri={}", baseUri);
    return URI.create(baseUri);
  }

  private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {
    // x-forwarded-host = Hostname of the API Gateway

    // "https" or "http"
    final var forwardedProto = request.getHeader(X_FORWARDED_PROTO);
    if (forwardedProto == null) {
      throw new IllegalStateException(STR."No \"\{X_FORWARDED_PROTO}\" header present");
    }

    var forwardedPrefix = request.getHeader(X_FORWARDED_PREFIX);
    // x-forwarded-prefix: null for Kubernetes Ingress Controller or "/customer" for Spring Cloud Gateway
    if (forwardedPrefix == null) {
      log.trace("getBaseUriForwarded: No \"{}\" header present", X_FORWARDED_PREFIX);
      forwardedPrefix = CUSTOMER_PREFIX;
    }
    final var baseUri = STR."\{forwardedProto}://\{forwardedHost}\{forwardedPrefix}\{CUSTOMER_PATH}";
    log.debug("getBaseUriForwarded: baseUri={}", baseUri);
    return URI.create(baseUri);
  }
}
