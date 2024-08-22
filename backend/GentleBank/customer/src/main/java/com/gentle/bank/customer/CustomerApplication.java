package com.gentle.bank.customer;

import com.gentle.bank.customer.config.ApplicationConfig;
import com.gentle.bank.customer.keycloak.KeycloakProps;
import com.gentle.bank.customer.util.MailProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.gentle.bank.customer.util.Banner.TEXT;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.support.WebStack.WEBMVC;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, com.gentle.bank.customer.dev.DevConfig.class})
@EnableConfigurationProperties({KeycloakProps.class, MailProps.class})
@EnableHypermediaSupport(type = HAL, stacks = WEBMVC)
@EnableJpaRepositories
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
@SuppressWarnings({"ImplicitSubclassInspection", "ClassUnconnectedToPackage"})
public class CustomerApplication {

  public static void main(String[] args) {
    final var app = new SpringApplication(CustomerApplication.class);
    app.setBanner((_,_, out) -> out.println(TEXT));
    app.run(args);
  }
}
