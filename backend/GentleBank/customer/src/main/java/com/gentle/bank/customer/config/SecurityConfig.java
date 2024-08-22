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

import static com.gentle.bank.customer.security.AuthController.AUTH_PATH;
import static com.gentle.bank.customer.security.Rolle.ADMIN;
import static com.gentle.bank.customer.util.Constants.CUSTOMER_PATH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

// https://github.com/spring-projects/spring-security/tree/master/samples
/**
 * Security-Konfiguration.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@SuppressWarnings("TrailingComment")
sealed interface SecurityConfig permits ApplicationConfig {
  /**
   * Bean-Methode zur Integration von Spring Security mit Keycloak.
   *
   * @return Post-Prozessor für Spring Security zur Integration mit Keycloak
   */
  @Bean
  default ResourceServerExpressionInterceptUrlRegistryPostProcessor authorizePostProcessor() {
    return registry -> registry
      .requestMatchers(OPTIONS, "/rest/**").permitAll()
      .requestMatchers("/rest/**").authenticated()
      .anyRequest().authenticated();
  }

  /**
   * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren,
   * d.h. vor Anwendung von @PreAuthorize.
   *
   * @param httpSecurity Injiziertes Objekt von HttpSecurity als Ausgangspunkt für die Konfiguration.
   * @param jwtAuthenticationConverter Injiziertes Objekt von Converter für die Anpassung an Keycloak
   * @return Objekt von SecurityFilterChain
   * @throws Exception Wegen HttpSecurity.authorizeHttpRequests()
   */
  // https://github.com/spring-projects/spring-security-samples/blob/main/servlet/java-configuration/...
  // ...authentication/preauth/src/main/java/example/SecurityConfiguration.java
  @Bean
  @SuppressWarnings("LambdaBodyLength")
  default SecurityFilterChain securityFilterChain(
    final HttpSecurity httpSecurity,
    final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter
  ) throws Exception {
    return httpSecurity
      .authorizeHttpRequests(authorize -> {
        final var restPathBestellungId = CUSTOMER_PATH + "/*";
        authorize
          .requestMatchers(POST,  "/login").permitAll()
          .requestMatchers(GET,  "/login").permitAll()
          .requestMatchers(OPTIONS, CUSTOMER_PATH + "/**").permitAll()
          .requestMatchers(GET, AUTH_PATH + "/me").hasRole(ADMIN.name())

          // https://spring.io/blog/2020/06/30/url-matching-with-pathpattern-in-spring-mvc
          // https://docs.spring.io/spring-security/reference/current/servlet/integrations/mvc.html
          .requestMatchers(GET, CUSTOMER_PATH, restPathBestellungId).permitAll()
          .requestMatchers(POST, restPathBestellungId, "/graphql").hasRole(ADMIN.name())


          .requestMatchers(POST, "/graphql", AUTH_PATH + "/login").permitAll()

          .requestMatchers(
            // Actuator: Health mit Liveness und Readiness fuer Kubernetes
            EndpointRequest.to(HealthEndpoint.class),
            // Actuator: Prometheus fuer Monitoring
            EndpointRequest.to(PrometheusScrapeEndpoint.class)
          ).permitAll()
          // OpenAPI bzw. Swagger UI und GraphiQL
          .requestMatchers(GET, "/v3/api-docs.yaml", "/v3/api-docs", "/graphiql").permitAll()
          .requestMatchers("/error", "/error/**").permitAll()

          .anyRequest().authenticated();
      })

      .oauth2ResourceServer(resourceServer -> resourceServer
        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter))
      )

      // Spring Security erzeugt keine HttpSession und verwendet keine fuer SecurityContext
      .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
      .formLogin(AbstractHttpConfigurer::disable)
      .csrf(AbstractHttpConfigurer::disable) // NOSONAR
      .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
      .build();
  }

  /**
   * Bean-Definition, um den Verschlüsselungsalgorithmus für Passwörter bereitzustellen. Es wird der
   * Default-Algorithmus von Spring Security verwendet: bcrypt.
   *
   * @return Objekt für die Verschlüsselung von Passwörtern.
   */
  @Bean
  default PasswordEncoder passwordEncoder() {
    return createDelegatingPasswordEncoder();
  }

@Bean
  default CompromisedPasswordChecker compromisedPasswordChecker() {
    return new HaveIBeenPwnedRestApiPasswordChecker();
  }
}
