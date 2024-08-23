package com.gentle.bank.customer.dev;

import org.springframework.context.annotation.Profile;

/**
 * Configures the development environment for the application.
 * <p>
 * This class is only activated when the "dev" Spring profile is active. It implements several
 * interfaces to provide specific development and debugging functionalities.
 * </p>
 * <ul>
 *   <li>{@link Flyway}: Provides a migration strategy for Flyway that deletes and recreates tables and indexes
 *   before migration.</li>
 *   <li>{@link LogRequestHeaders}: Configures a filter to log the headers of incoming requests.</li>
 *   <li>{@link LogPasswordEncoding}: Logs the encryption of passwords at application startup.</li>
 *   <li>{@link LogSignatureAlgorithms}: Logs the available signature algorithms from the JDK.</li>
 *   <li>{@link K8s}: Logs when the application is running in a Kubernetes environment.</li>
 * </ul>
 * <p>
 * This class is automatically configured at runtime to ensure that all specific
 * development features are enabled when the "dev" profile is used.
 * </p>
 *
 * @see Flyway
 * @see LogRequestHeaders
 * @see LogPasswordEncoding
 * @see LogSignatureAlgorithms
 * @see K8s
 *
 * @author Caleb Gyamfi
 * @version 1.0
 * @since 23.08.2024
 */
@Profile(DevConfig.DEV)
public class DevConfig implements Flyway, LogRequestHeaders, LogPasswordEncoding, LogSignatureAlgorithms, K8s {

  /**
   * Constant for the Spring profile "dev".
   * <p>
   * This profile is used to ensure that development and debugging-specific
   * configurations are loaded.
   * </p>
   */
  public static final String DEV = "dev";

  /**
   * Constructor for the DevConfig class.
   * <p>
   * The default constructor is used to create an instance of the DevConfig class. Since this class
   * is only used for the "dev" profile, the constructor is only invoked in a development environment.
   * </p>
   */
  DevConfig() {
  }
}
