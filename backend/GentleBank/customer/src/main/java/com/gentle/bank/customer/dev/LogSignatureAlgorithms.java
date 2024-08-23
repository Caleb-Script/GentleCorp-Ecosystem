package com.gentle.bank.customer.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

/**
 * Provides functionality to log the signature algorithms available in the Java Development Kit (JDK).
 * <p>
 * This interface defines a bean that listens for the {@link ApplicationReadyEvent} and logs all available
 * signature algorithms registered in the JDK's security providers.
 * </p>
 * <p>
 * The logging of signature algorithms is only enabled when the "logSecurity" profile is active.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Logs available signature algorithms for debugging or security auditing purposes.</li>
 *   <li>Conditional activation based on the "logSecurity" profile.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * When the application is running with the "logSecurity" profile, this bean will log all signature algorithms
 * available in the JDK. This is useful for security auditing and debugging purposes to verify which algorithms
 * are supported.
 * </p>
 *
 * @see ApplicationListener
 * @see ApplicationReadyEvent
 * @see Logger
 *
 * @author Caleb Gyamfi
 * @version 1.0
 * @since 23.08.2024
 */
public interface LogSignatureAlgorithms {

  /**
   * Bean definition that provides an {@link ApplicationListener} for logging available signature algorithms
   * when the application is ready.
   * <p>
   * This listener iterates over the security providers configured in the JDK and logs the algorithms
   * of type "Signature" for each provider.
   * </p>
   *
   * @return an {@link ApplicationListener} that logs the signature algorithms
   */
  @Bean
  @Profile("logSecurity")
  default ApplicationListener<ApplicationReadyEvent> logSignatureAlgorithms() {
    final var log = LoggerFactory.getLogger(LogSignatureAlgorithms.class);
    return event -> Arrays
      .stream(Security.getProviders())
      .forEach(provider -> logSignatureAlgorithms(provider, log));
  }

  /**
   * Logs the signature algorithms provided by a specific {@link Provider}.
   * <p>
   * This private method is used to extract and log algorithms of type "Signature" from the provided
   * {@link Provider}.
   * </p>
   *
   * @param provider the security provider from which to log signature algorithms
   * @param log the {@link Logger} instance used for logging
   */
  private void logSignatureAlgorithms(final Provider provider, final Logger log) {
    provider
      .getServices()
      .forEach(service -> {
        if ("Signature".contentEquals(service.getType())) {
          log.debug("{}", service.getAlgorithm());
        }
      });
  }
}
