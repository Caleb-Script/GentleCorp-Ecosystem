package com.gentle.bank.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for mail settings in the application.
 * <p>
 * This class is used to map the properties defined in the application's configuration file
 * (e.g., `application.properties` or `application.yml`) with the prefix `app.mail` to the
 * fields of this class. It contains properties related to email configuration such as the
 * sender and recipient email addresses.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@ConfigurationProperties(prefix = "app.mail")
@Setter
@Getter
@AllArgsConstructor
public class MailProps {

  /**
   * The email address from which emails will be sent.
   */
  private String from;

  /**
   * The email address to which emails will be sent.
   */
  private String to;
}
