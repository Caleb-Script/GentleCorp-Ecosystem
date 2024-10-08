package com.gentle.bank.customer.config;

import com.c4_soft.springaddons.security.oidc.starter.synchronised.resourceserver.ResourceServerExpressionInterceptUrlRegistryPostProcessor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import static com.gentle.bank.customer.controller.AuthController.AUTH_PATH;
import static com.gentle.bank.customer.entity.enums.RoleType.ELITE;
import static com.gentle.bank.customer.entity.enums.RoleType.ESSENTIAL;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLECORP_ADMIN;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLECORP_USER;
import static com.gentle.bank.customer.util.Constants.CUSTOMER_PATH;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

/**
 * Security configuration for the application.
 * <p>
 * This interface defines security configurations including authorization rules, password encoding,
 * and settings for handling security contexts and sessions. It is designed to be implemented by
 * {@link ApplicationConfig}.
 * </p>
 *
 * @since 2024-08-24
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 * @see ApplicationConfig
 */
@SuppressWarnings("TrailingComment")
sealed interface SecurityConfig permits ApplicationConfig {

  /**
   * Bean method to integrate Spring Security with Keycloak.
   * <p>
   * This method returns a {@link ResourceServerExpressionInterceptUrlRegistryPostProcessor} that configures
   * URL patterns for authentication and authorization rules. It permits all requests to {@code /rest/**} with
   * OPTIONS method and ensures all other requests are authenticated.
   * </p>
   *
   * @return a post-processor for Spring Security to integrate with Keycloak
   * @since 2024-08-24
   */
  @Bean
  default ResourceServerExpressionInterceptUrlRegistryPostProcessor authorizePostProcessor() {
    return registry -> registry
      .requestMatchers(OPTIONS, "/rest/**").permitAll()
      .requestMatchers("/rest/**").authenticated()
      .anyRequest().authenticated();
  }

  /**
   * Bean definition to configure access control at REST endpoints before applying {@code @PreAuthorize}.
   * <p>
   * This method configures {@link HttpSecurity} to define authorization rules for various endpoints,
   * including support for JWT authentication and session management. It also sets up security for actuator
   * endpoints and Swagger UI.
   * </p>
   *
   * @param httpSecurity the injected {@link HttpSecurity} object used as a starting point for configuration
   * @param jwtAuthenticationConverter the injected {@link Converter} for JWT to Keycloak authentication
   * @return an instance of {@link SecurityFilterChain}
   * @throws Exception if an error occurs during {@link HttpSecurity#authorizeHttpRequests()}
   * @since 2024-08-24
   */
  @Bean
  @SuppressWarnings("LambdaBodyLength")
  default SecurityFilterChain securityFilterChain(
    final HttpSecurity httpSecurity,
    final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter
  ) throws Exception {
    return httpSecurity
      .authorizeHttpRequests(authorize -> {
        authorize
          .requestMatchers(POST, "/auth/login").permitAll()

          .requestMatchers(GET, CUSTOMER_PATH).hasAnyRole(GENTLECORP_USER.getRole(), GENTLECORP_ADMIN.getRole())
          .requestMatchers(GET, CUSTOMER_PATH + "/**").permitAll()
          .requestMatchers(POST, CUSTOMER_PATH).permitAll()
          .requestMatchers(PUT, CUSTOMER_PATH + "**").hasAnyRole(GENTLECORP_ADMIN.getRole(), GENTLECORP_USER.getRole(), ELITE.getRole(), ESSENTIAL.getRole())
          .requestMatchers(DELETE, CUSTOMER_PATH + "/**").hasAnyRole(GENTLECORP_ADMIN.getRole())

          .requestMatchers(GET, AUTH_PATH + "/me").hasRole(GENTLECORP_ADMIN.getRole())

          .requestMatchers(POST, AUTH_PATH + "/login").permitAll()

          .requestMatchers(
            // Actuator: Health for liveness and readiness for Kubernetes
            EndpointRequest.to(HealthEndpoint.class),
            // Actuator: Prometheus for monitoring
            EndpointRequest.to(PrometheusScrapeEndpoint.class)
          ).permitAll()
          // OpenAPI or Swagger UI and GraphiQL
          .requestMatchers(GET, "/v3/api-docs.yaml", "/v3/api-docs", "/graphiql").permitAll()
          .requestMatchers("/error", "/error/**").permitAll()

          .anyRequest().authenticated();
      })

      .oauth2ResourceServer(resourceServer -> resourceServer
        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter))
      )

      // Spring Security does not create or use HttpSession for SecurityContext
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .formLogin(AbstractHttpConfigurer::disable)
      .csrf(AbstractHttpConfigurer::disable) // NOSONAR
      .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
      .build();
  }

  /**
   * Bean definition to provide the password encryption algorithm. The default algorithm provided by Spring Security, bcrypt, is used.
   *
   * @return a {@link PasswordEncoder} instance for password encryption
   * @since 2024-08-24
   */
  @Bean
  default PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  /**
   * Bean definition to provide a checker for compromised passwords using the Have I Been Pwned API.
   *
   * @return a {@link CompromisedPasswordChecker} instance
   * @since 2024-08-24
   */
  @Bean
  default CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
