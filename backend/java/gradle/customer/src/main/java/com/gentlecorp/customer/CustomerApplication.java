package com.gentlecorp.customer;

import com.gentlecorp.customer.config.ApplicationConfig;
import com.gentlecorp.customer.dev.DevConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.gentlecorp.customer.util.Banner.TEXT;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.support.WebStack.WEBMVC;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, DevConfig.class})
@EnableConfigurationProperties({KeycloakProps.class, MailProps.class})
@EnableHypermediaSupport(type = HAL, stacks = WEBMVC)
@EnableJpaRepositories
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
@SuppressWarnings({"ClassUnconnectedToPackage"})
@EntityScan
public class CustomerApplication {

  /**
   * The main method that serves as the entry point for the Spring Boot application.
   * <p>
   * This method initializes the Spring application context, sets the banner to be displayed on startup,
   * and runs the application with the given arguments.
   * </p>
   *
   * @param args Command-line arguments passed to the application at startup.
   */
  public static void main(String[] args) {
    final var app = new SpringApplication(CustomerApplication.class);
    app.setBanner((_, _, out) -> out.println(TEXT));
    app.run(args);
  }
}
