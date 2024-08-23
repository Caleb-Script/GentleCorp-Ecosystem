package com.gentle.bank.customer.dev;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import static org.springframework.boot.cloud.CloudPlatform.KUBERNETES;

/**
 * Interface for detecting and logging Kubernetes platform presence in a Spring Boot application.
 * <p>
 * This interface provides a method to create a bean that listens for the {@link ApplicationReadyEvent}
 * and logs a message if the application is running on a Kubernetes platform. It uses Spring's
 * {@link ConditionalOnCloudPlatform} annotation to conditionally create the bean only when the
 * application is deployed on Kubernetes.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Logs a message indicating that the application is running on Kubernetes.</li>
 *   <li>Uses Spring Boot's conditional configuration to ensure the bean is only created in the appropriate environment.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * This interface is intended to be implemented or used in situations where it is necessary to
 * confirm and log the presence of a Kubernetes platform. The `detectK8s` method is defined as
 * a default method, making it easy to integrate this functionality into a Spring Boot application.
 * </p>
 *
 * @author Caleb Gyamfi
 * @version 1.0
 * @since 23.08.2024
 */
interface K8s {

  /**
   * Logs a message when Kubernetes is detected.
   * <p>
   * This method defines a bean that listens for the {@link ApplicationReadyEvent} and logs a debug
   * message indicating that the application is running on the Kubernetes platform. The bean is
   * only created if the application is detected to be running on Kubernetes, as determined by
   * the {@link ConditionalOnCloudPlatform} annotation.
   * </p>
   *
   * @return an {@link ApplicationListener} for the {@link ApplicationReadyEvent} that logs the
   *         presence of Kubernetes.
   */
  @Bean
  @ConditionalOnCloudPlatform(KUBERNETES)
  default ApplicationListener<ApplicationReadyEvent> detectK8s() {
    return event -> LoggerFactory.getLogger(K8s.class).debug("Plattform \"Kubernetes\"");
  }
}
