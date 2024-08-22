
package com.gentle.bank.customer.keycloak;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Spring-Konfiguration für Properties "app.keycloak.*".
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @param host Rechnername des Keycloak-Servers
 * @param clientId client-id gemäß der Client-Konfiguration in Keycloak
 * @param clientSecret Client-Secret gemäß der Client-Konfiguration in Keycloak
 * @param port der port auf dem keycloak läuft
 * @param schema http
 */
@ConfigurationProperties(prefix = "app.keycloak")
public record KeycloakProps(
    @DefaultValue("http")
    String schema,

    @DefaultValue("localhost")
    String host,

    @DefaultValue("8880")
    int port,

    @DefaultValue("GentleBank")
    String clientId,

    @DefaultValue("6J3uzo8E8jbCUbiraQIAOKuPRA4xKO7S")
    String clientSecret) {
}
