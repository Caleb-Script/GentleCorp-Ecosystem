package com.gentlecorp.transaction.config;

import com.c4_soft.springaddons.security.oidc.starter.synchronised.resourceserver.ResourceServerExpressionInterceptUrlRegistryPostProcessor;
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

import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder;

sealed interface SecurityConfig permits ApplicationConfig {
  @Bean
  default ResourceServerExpressionInterceptUrlRegistryPostProcessor authorizePostProcessor() {
    return registry -> registry
      .requestMatchers(OPTIONS, "/rest/**").permitAll()
      .requestMatchers("/rest/**").authenticated()
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
          .anyRequest().permitAll();
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
