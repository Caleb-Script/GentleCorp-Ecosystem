package com.gentle.bank.customer;

import com.gentle.bank.customer.config.ApplicationConfig;
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

/**
 * The entry point for the Customer application.
 * <p>
 * This class sets up and runs the Spring Boot application for the Customer service. It includes configuration
 * for various aspects of the application, such as enabling JPA repositories, web security, method security,
 * asynchronous processing, and hypermedia support. It also sets the application's banner and imports additional
 * configuration classes.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
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
