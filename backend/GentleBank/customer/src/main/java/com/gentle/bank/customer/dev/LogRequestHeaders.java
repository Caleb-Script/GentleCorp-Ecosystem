package com.gentle.bank.customer.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Interface for configuring request logging in a Spring Boot application.
 * <p>
 * This interface provides a method to create a {@link CommonsRequestLoggingFilter} bean,
 * which logs the request headers and query parameters. This can be useful for debugging
 * and monitoring HTTP requests by capturing detailed information about incoming requests.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Configures a filter to log request headers and query strings.</li>
 *   <li>Helps in troubleshooting and monitoring HTTP requests by providing visibility into the request details.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * The `logFilter` method is defined as a default method to facilitate easy integration
 * into a Spring Boot application. The created filter can be registered with the application's
 * filter chain to log request details automatically.
 * </p>
 *
 * @author Caleb Gyamfi
 * @version 1.0
 * @since 23.08.2024
 */
interface LogRequestHeaders {
  /**
   * Creates a {@link CommonsRequestLoggingFilter} bean for logging request headers and query strings.
   * <p>
   * This method sets up a {@link CommonsRequestLoggingFilter} to include query strings and request headers
   * in the log output. It helps in capturing detailed information about incoming HTTP requests,
   * which can be valuable for debugging and monitoring purposes.
   * </p>
   *
   * @return a {@link CommonsRequestLoggingFilter} configured to log request headers and query strings.
   */
  @Bean
  default CommonsRequestLoggingFilter logFilter() {
    final var filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(true);
    filter.setIncludeHeaders(true);
    return filter;
  }
}
