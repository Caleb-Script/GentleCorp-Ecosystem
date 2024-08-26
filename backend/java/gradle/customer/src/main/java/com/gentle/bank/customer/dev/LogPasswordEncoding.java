package com.gentle.bank.customer.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Interface for logging password encoding processes using a specified {@link PasswordEncoder}.
 * <p>
 * This interface provides a method to create a Spring Bean that listens for the {@link ApplicationReadyEvent}
 * and logs the encoded form of a specified password using the configured {@link PasswordEncoder}.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Logs the encoded version of a password, demonstrating the configured {@link PasswordEncoder}'s behavior.</li>
 *   <li>Uses Spring's {@link Value} annotation to inject the password to be encoded.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * The `logPasswordEncoding` method is defined as a default method, making it easy to integrate
 * this functionality into a Spring Boot application. It can be particularly useful for verifying
 * password encoding configurations during application startup.
 * </p>
 *
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 * @since 24.08.2024
 */
interface LogPasswordEncoding {
  Logger LOGGER = LoggerFactory.getLogger(LogPasswordEncoding.class);

  /**
   * Logs the encoded form of a password using the configured {@link PasswordEncoder}.
   * <p>
   * This method defines a bean that listens for the {@link ApplicationReadyEvent} and logs
   * the encoded version of a specified password. The password is injected via the {@link Value} annotation
   * from the application's configuration.
   * </p>
   *
   * @param passwordEncoder the {@link PasswordEncoder} to be used for encoding the password.
   * @param password the password to be encoded, injected from the application's configuration.
   * @return an {@link ApplicationListener} for the {@link ApplicationReadyEvent} that logs the encoded password.
   */
  @Bean
  default ApplicationListener<ApplicationReadyEvent> logPasswordEncoding(
    final PasswordEncoder passwordEncoder,
    @Value("${app.password}") final String password
  ) {
    return event -> {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Argon2id with password \"{}\": {}", password, passwordEncoder.encode(password));
      }
    };
  }
}
