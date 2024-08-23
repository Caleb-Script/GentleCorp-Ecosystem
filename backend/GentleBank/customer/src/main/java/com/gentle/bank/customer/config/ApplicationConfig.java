package com.gentle.bank.customer.config;

/**
 * The {@code ApplicationConfig} class serves as the main configuration class
 * for the application. This class implements both {@link SecurityConfig} and
 * {@link KeycloakClientConfig} interfaces, indicating that it provides
 * the necessary configurations for security and Keycloak client settings.
 *
 * <p>This class is declared as {@code final}, meaning it cannot be subclassed.
 * The constructor is package-private, preventing instantiation from outside
 * the package.</p>
 *
 * <p>Note: As this class currently does not contain any methods or properties,
 * its purpose is mainly as a marker or placeholder for future configurations.</p>
 *
 * @since 23.08.2024
 * @author Caleb Gyamfi
 * @see SecurityConfig
 * @see KeycloakClientConfig
 */
public final class ApplicationConfig implements SecurityConfig, KeycloakClientConfig {

  /**
   * Package-private constructor to prevent external instantiation.
   * This constructor is intentionally left blank.
   */
  ApplicationConfig() {
  }
}
