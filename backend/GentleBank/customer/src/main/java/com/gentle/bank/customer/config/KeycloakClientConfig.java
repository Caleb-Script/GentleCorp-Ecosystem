package com.gentle.bank.customer.config;

import com.gentle.bank.customer.repository.KeycloakRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Configuration for the Spring HTTP client used to access the Keycloak service.
 * <p>
 * This interface defines a method for configuring a {@link KeycloakRepository} client.
 * The implementation of this interface creates a bean that will be used for communication
 * with the Keycloak service.
 * </p>
 * <p>
 * The interface is {@code sealed} and only allows implementation by {@link ApplicationConfig}.
 * </p>
 *
 * @since 2024-08-24
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @see ApplicationConfig
 * @see KeycloakRepository
 */
sealed interface KeycloakClientConfig permits ApplicationConfig {

  /**
   * Logger object for the {@code KeycloakClientConfig} interface.
   * <p>
   * This logger object is used to log debug and other messages related to
   * the Keycloak client configuration.
   * </p>
   */
  Logger LOGGER = LoggerFactory.getLogger(KeycloakClientConfig.class);

  /**
   * Creates a {@link KeycloakRepository} bean for communication with the Keycloak service.
   * <p>
   * This method configures the {@code RestClient} with the base URL defined by environment variables.
   * The client is then adapted into a {@code RestClientAdapter}, and an {@code HttpServiceProxyFactory}
   * is used to create a proxy for the {@link KeycloakRepository} interface.
   * </p>
   *
   * @param clientBuilder the {@link RestClient.Builder} used to create the {@code RestClient}
   * @return an instance of {@link KeycloakRepository} used for interacting with the Keycloak service
   * @since 2024-08-24
   */
  @Bean
  default KeycloakRepository keycloakRepository(
    final RestClient.Builder clientBuilder
  ) {
    final var kcDefaultPort = 8880;

    final var kcSchemaEnv = System.getenv("KC_SERVICE_SCHEMA");
    final var kcHostEnv = System.getenv("KC_SERVICE_HOST");
    final var kcPortEnv = System.getenv("KC_SERVICE_PORT");

    final var schema = kcSchemaEnv == null ? "http" : kcSchemaEnv;
    final var host = kcHostEnv == null ? "localhost" : kcHostEnv;
    final int port = kcPortEnv == null ? kcDefaultPort : Integer.parseInt(kcPortEnv);
    final var baseUri = UriComponentsBuilder.newInstance()
      .scheme(schema)
      .host(host)
      .port(port)
      .build();
    LOGGER.debug("keycloakRepository: baseUri={}", baseUri);

    final var restClient = clientBuilder.baseUrl(baseUri.toUriString()).build();
    final var clientAdapter = RestClientAdapter.create(restClient);
    final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
    return proxyFactory.createClient(KeycloakRepository.class);
  }
}
