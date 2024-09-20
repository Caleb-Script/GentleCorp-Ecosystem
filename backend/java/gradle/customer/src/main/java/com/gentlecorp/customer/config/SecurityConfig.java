package com.gentlecorp.customer.config;

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

import static com.gentlecorp.customer.model.enums.RoleType.ADMIN;
import static com.gentlecorp.customer.model.enums.RoleType.BASIC;
import static com.gentlecorp.customer.model.enums.RoleType.ELITE;
import static com.gentlecorp.customer.model.enums.RoleType.SUPREME;
import static com.gentlecorp.customer.model.enums.RoleType.USER;
import static com.gentlecorp.customer.util.Constants.AUTH_PATH;
import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

sealed interface SecurityConfig permits ApplicationConfig {
  @Bean
  default ResourceServerExpressionInterceptUrlRegistryPostProcessor authorizePostProcessor() {
    return registry -> registry
      .requestMatchers(OPTIONS, CUSTOMER_PATH+"/**").permitAll()
      .requestMatchers(CUSTOMER_PATH+"/**").authenticated()
      .anyRequest().authenticated();
  }

  @Bean
  default SecurityFilterChain securityFilterChain(
    final HttpSecurity httpSecurity,
    final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter
  ) throws Exception {
    return httpSecurity
      .authorizeHttpRequests(authorize -> {
        authorize
          .requestMatchers(GET, CUSTOMER_PATH + "/hallo").permitAll()
          .requestMatchers(GET, CUSTOMER_PATH).hasAnyRole(USER.name(), ADMIN.name())
          .requestMatchers(GET, CUSTOMER_PATH + "/all/**").hasAnyRole(ADMIN.name(), USER.name())
          .requestMatchers(GET, CUSTOMER_PATH + "/**").hasAnyRole(ADMIN.name(), USER.name(),SUPREME.name(), ELITE.name(), BASIC.name())
          .requestMatchers(POST, CUSTOMER_PATH).permitAll()
          .requestMatchers(PUT, CUSTOMER_PATH + "**").hasAnyRole(ADMIN.name(),SUPREME.name(), ELITE.name(), BASIC.name())
          .requestMatchers(DELETE, CUSTOMER_PATH + "/**").hasAnyRole(ADMIN.name())

          .requestMatchers(GET, AUTH_PATH + "/me").hasRole(ADMIN.name())
          .requestMatchers(POST, AUTH_PATH + "/login").permitAll()

          .requestMatchers(POST,"dev/db_populate").hasRole(ADMIN.name())
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

  @Bean
  default PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

  @Bean
  default CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
