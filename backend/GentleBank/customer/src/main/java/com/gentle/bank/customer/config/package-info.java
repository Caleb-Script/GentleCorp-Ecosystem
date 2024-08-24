/**
 * Provides the configuration classes for the application.
 * <p>
 * This package contains classes and interfaces responsible for configuring
 * various aspects of the application, including security settings and client
 * communication with external services such as Keycloak.
 * </p>
 * <p>
 * Key components of this package include:
 * <ul>
 *     <li>{@link SecurityConfig}: Configures security settings including authentication and authorization.</li>
 *     <li>{@link com.gentle.bank.customer.config.KeycloakClientConfig}: Configures the HTTP client used to communicate with the Keycloak service.</li>
 *     <li>{@link com.gentle.bank.customer.config.ApplicationConfig}: Implements both {@code SecurityConfig} and {@code KeycloakClientConfig}, serving as the main configuration class for the application.</li>
 * </ul>
 * </p>
 * <p>
 * Classes within this package are generally intended to be used internally within
 * the application and are not typically exposed to external clients.
 * </p>
 *
 * @since 2024-08-24
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @see com.gentle.bank.customer.config.SecurityConfig
 * @see com.gentle.bank.customer.config.KeycloakClientConfig
 * @see com.gentle.bank.customer.config.ApplicationConfig
 */
package com.gentle.bank.customer.config;
