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

@Service
@RequiredArgsConstructor
@Slf4j
public class Mailer {
    private static final boolean SMTP_ACTIVATED = Objects.equals(System.getenv("SMTP_ACTIVATED"), "true") ||
        Objects.equals(System.getProperty("smtp-activated"), "true");

    private final JavaMailSender mailSender;
    private final MailProps props;

    @Value("${spring.mail.host}")
    private String mailhost;

    /**
     * Email senden, dass es einen neuen Passengers gibt.
     *
     * @param neuerKunde Das Objekt des neuen Passengers.
     */
    @Async
    public void send(final Customer neuerKunde) {
        if (!SMTP_ACTIVATED) {
            log.warn("SMTP ist deaktiviert.");
        }
        final MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setFrom(new InternetAddress(props.getFrom()));
            mimeMessage.setRecipient(TO, new InternetAddress(props.getTo()));
            mimeMessage.setSubject(STR."Neuer Kunde \{neuerKunde.getId()}");
            final var body = STR."<strong>Neuer Passenger:</strong> <em>\{neuerKunde.getLastName()}</em>";
            log.trace("send: Mailserver={}, Thread-ID={}, body={}", mailhost, Thread.currentThread().threadId(), body);
            mimeMessage.setText(body);
            mimeMessage.setHeader("Content-Type", "text/html");
        };

        try {
            mailSender.send(preparator);
        } catch (final MailSendException | MailAuthenticationException e) {
            // TODO Wiederholung, um die Email zu senden
            log.warn("Email nicht gesendet: Ist der Mailserver {} erreichbar?", mailhost);
        }
    }
}
