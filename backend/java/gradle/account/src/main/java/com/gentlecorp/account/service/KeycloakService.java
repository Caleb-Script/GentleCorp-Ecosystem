package com.gentlecorp.account.service;

import com.gentlecorp.account.KeycloakProps;
import com.gentlecorp.account.model.dto.TokenDTO;
import com.gentlecorp.account.repository.KeycloakRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

  private final KeycloakRepository keycloakRepository;
  private final KeycloakProps keycloakProps;

  public TokenDTO login(final String username, final String password) {
    return keycloakRepository.login(
      "grant_type=password&username=" + username
        + "&password=" + password
        + "&client_id=" + keycloakProps.clientId()
        + "&client_secret=" + keycloakProps.clientSecret()
        + "&scope=openid",
      APPLICATION_FORM_URLENCODED_VALUE
    );
  }
}
