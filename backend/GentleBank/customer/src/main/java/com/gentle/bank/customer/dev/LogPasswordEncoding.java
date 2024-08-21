package com.gentle.bank.customer.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

interface LogPasswordEncoding {
    Logger LOGGER = LoggerFactory.getLogger(LogPasswordEncoding.class);
    /**
     * Bean-Definition, um einen Listener bereitzustellen, der verschiedene Verschlüsselungsverfahren ausgibt.
     *
     * @param passwordEncoder PasswordEncoder für Argon2
     * @param password Das zu verschlüsselnde Passwort
     * @return Listener für die Ausgabe der verschiedenen Verschlüsselungsverfahren
     */
    @Bean
    default ApplicationListener<ApplicationReadyEvent> logPasswordEncoding(
        final PasswordEncoder passwordEncoder,
        @Value("${app.password}") final String password
    ) {
        //noinspection unused
        return event -> {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Argon2id mit \"{}\":   {}", password, passwordEncoder.encode(password));
                }
            };
    }
}
