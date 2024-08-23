/**
 * Contains configuration classes for the application.
 *
 * <p>This package includes classes that define and manage various configurations needed by the application.
 * It includes security settings, client configurations, and other application-specific settings that
 * are essential for the proper functioning of the application.</p>
 *
 * <p>The classes in this package are responsible for setting up security configurations using Spring Security,
 * configuring clients for services like Keycloak, and managing application-specific properties and beans.
 * The configurations defined here ensure that the application adheres to security best practices and
 * integrates seamlessly with external systems and services.</p>
 *
 * <p>Classes in this package:
 * <ul>
 *   <li>{@link com.gentle.bank.customer.config.ApplicationConfig} - Provides general application configurations including Keycloak client setup.</li>
 *   <li>{@link com.gentle.bank.customer.config.KeycloakClientConfig} - Configures the HTTP client for interacting with Keycloak.</li>
 *   <li>{@link com.gentle.bank.customer.config.SecurityConfig} - Configures security settings for the application, including access control and password management.</li>
 * </ul>
 * </p>
 *
 * @since 23.08.2024
 * @author Caleb Gyamfi
 * @see com.gentle.bank.customer.config.ApplicationConfig
 * @see com.gentle.bank.customer.config.KeycloakClientConfig
 * @see com.gentle.bank.customer.config.SecurityConfig
 */
package com.gentle.bank.customer.config;
