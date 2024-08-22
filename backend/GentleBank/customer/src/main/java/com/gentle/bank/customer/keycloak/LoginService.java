package com.gentle.bank.customer.keycloak;

import com.gentle.bank.customer.security.KeycloakRepository;
import com.gentle.bank.customer.security.TokenDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

/**
 * Javadoc-Kommentar für die LoginService-Klasse.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final KeycloakRepository keycloakRepository;
    private final KeycloakProps keycloakProps;
    private String clientAndSecretEncoded;

    @PostConstruct
    private void encodeClientAndSecret() {
        final var clientAndSecret = keycloakProps.clientId() + ':' + keycloakProps.clientSecret();
        clientAndSecretEncoded = Base64
            .getEncoder()
            .encodeToString(clientAndSecret.getBytes(Charset.defaultCharset()));
    }

    /**
     * login funktion zum Anmelden.
     *
     * @param username username des benutzers.
     *
     * @return accessToken und etc...
     */
    public TokenDTO login(final String username, final String password) {
        log.debug("login: username={}", username);

        final var tokenDTO = keycloakRepository.login(
            "grant_type=password&username=" + username +
                "&password=" + password + "&client_id=" + keycloakProps.clientId() +
                "&client_secret" + keycloakProps.clientSecret(),
            "Basic " + clientAndSecretEncoded,
            APPLICATION_FORM_URLENCODED_VALUE
        );

        log.debug("token: " + tokenDTO);
        return tokenDTO;
    }
}
