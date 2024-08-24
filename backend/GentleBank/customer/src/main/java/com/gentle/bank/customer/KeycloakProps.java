package com.gentle.bank.customer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Keycloak settings.
 * <p>
 * This record class is used to map the properties defined in the application's configuration file
 * (e.g., `application.properties` or `application.yml`) with the prefix `app.keycloak` to the
 * fields of this class. It contains properties related to the Keycloak server configuration, such as
 * the schema, host, port, client ID, and client secret.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 *
 * @param schema The schema to use (e.g., "http" or "https").
 * @param host The hostname of the Keycloak server.
 * @param port The port on which the Keycloak server is running.
 * @param clientId The client ID as defined in the Keycloak client configuration.
 * @param clientSecret The client secret as defined in the Keycloak client configuration.
 */
@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProps(
  String schema,

  String host,

  int port,

  String clientId,

  String clientSecret
) {
}
