package com.gentle.bank.customer.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * A {@link ControllerAdvice} implementation that logs the response body for debugging purposes.
 * <p>
 * This class is activated only when the "log-body" profile is active. It implements {@link ResponseBodyAdvice}
 * to intercept and log the response body of all REST API responses.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Logs the response body at TRACE level for detailed debugging.</li>
 *   <li>Active only when the "log-body" profile is enabled, ensuring that logging is conditional.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * When the application is running with the "log-body" profile, this class will log the response body of
 * each HTTP response. This can be useful for debugging and inspecting the content of responses during development.
 * </p>
 *
 * @see ResponseBodyAdvice
 * @see ControllerAdvice
 * @see LogRequestHeaders
 *
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 * @since 24.08.2024
 */
@ControllerAdvice
@Profile("log-body")
@Slf4j
public class LogResponseBody implements ResponseBodyAdvice<Object> {

  /**
   * Determines whether this {@link ResponseBodyAdvice} implementation should be applied
   * to the given method and converter type.
   * <p>
   * In this implementation, it always returns {@code true}, meaning it applies to all responses.
   * </p>
   *
   * @param returnType the method return type
   * @param converterType the selected {@link HttpMessageConverter} type
   * @return {@code true} to apply this advice, {@code false} otherwise
   */
  @Override
  public boolean supports(
    final MethodParameter returnType,
    final Class<? extends HttpMessageConverter<?>> converterType
  ) {
    return true;
  }

  /**
   * Logs the response body before it is written to the HTTP response.
   * <p>
   * This method logs the body at TRACE level for detailed debugging purposes and then returns the body unchanged.
   * </p>
   *
   * @param body the response body to be logged
   * @param returnType the method return type
   * @param selectedContentType the selected {@link MediaType} for the response
   * @param selectedConverterType the selected {@link HttpMessageConverter} type
   * @param request the current HTTP request
   * @param response the current HTTP response
   * @return the response body (unchanged)
   */
  @Override
  public Object beforeBodyWrite(
    final Object body,
    final MethodParameter returnType,
    final MediaType selectedContentType,
    final Class<? extends HttpMessageConverter<?>> selectedConverterType,
    final ServerHttpRequest request,
    final ServerHttpResponse response
  ) {
    log.trace("{}", body);
    return body;
  }
}
