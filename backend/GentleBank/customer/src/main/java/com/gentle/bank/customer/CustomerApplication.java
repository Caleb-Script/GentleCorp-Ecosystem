package com.gentle.bank.customer;

import com.gentle.bank.customer.config.ApplicationConfig;
import com.gentle.bank.customer.util.MailProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import static com.gentle.bank.customer.util.Banner.TEXT;

@SpringBootApplication(proxyBeanMethods = false)
@Import({ApplicationConfig.class, com.gentle.bank.customer.dev.DevConfig.class})
@EnableConfigurationProperties(MailProps.class)
public class CustomerApplication {

  public static void main(String[] args) {
    final var app = new SpringApplication(CustomerApplication.class);
    app.setBanner((environment, sourceClass, out) -> out.println(TEXT));
    app.run(args);
  }
}
