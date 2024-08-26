package com.gentle.bank.customer.service;

import com.gentle.bank.customer.MailProps;
import com.gentle.bank.customer.entity.Customer;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static jakarta.mail.Message.RecipientType.TO;

/**
 * Service class for sending emails.
 * <p>
 * This service handles sending emails using the configured SMTP server. It prepares and sends an email notification when a new customer (passenger) is created.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

  private static final boolean SMTP_ACTIVATED = Objects.equals(System.getenv("SMTP_ACTIVATED"), "true") ||
    Objects.equals(System.getProperty("smtp-activated"), "true");

  private final JavaMailSender mailSender;
  private final MailProps props;

  @Value("${spring.mail.host}")
  private String mailhost;

  /**
   * Sends an email notification about a new customer.
   * <p>
   * This method is asynchronous and will send an email to the configured recipient with information about the newly created customer.
   * The email will be formatted as HTML.
   * </p>
   *
   * @param neuerKunde the {@link Customer} object representing the new customer.
   */
  @Async
  public void send(final Customer neuerKunde) {
    if (!SMTP_ACTIVATED) {
      log.warn("SMTP is disabled.");
      return;
    }

    final MimeMessagePreparator preparator = mimeMessage -> {
      mimeMessage.setFrom(new InternetAddress(props.getFrom()));
      mimeMessage.setRecipient(TO, new InternetAddress(props.getTo()));
      mimeMessage.setSubject(String.format("New Customer %s", neuerKunde.getId()));
      final var body = String.format("<strong>New Customer:</strong> <em>%s</em>", neuerKunde.getLastName());
      log.trace("send: Mail server={}, Thread ID={}, body={}", mailhost, Thread.currentThread().getId(), body);
      mimeMessage.setText(body, "UTF-8", "html");
    };

    try {
      mailSender.send(preparator);
    } catch (final MailSendException | MailAuthenticationException e) {
      // TODO: Add retry logic for sending the email
      log.warn("Email not sent: Is the mail server {} reachable?", mailhost);
    }
  }
}
