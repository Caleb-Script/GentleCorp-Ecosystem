package com.gentlecorp.invoice.controller;

import com.gentlecorp.invoice.exception.UnauthorizedException;
import com.gentlecorp.invoice.model.dto.LoginDTO;
import com.gentlecorp.invoice.model.dto.TokenDTO;
import com.gentlecorp.invoice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static com.gentlecorp.invoice.util.Constants.AUTH_PATH;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping(AUTH_PATH)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S1075")
public class AuthController {

  private final CompromisedPasswordChecker passwordChecker;
  private final KeycloakService keycloakService;

  @GetMapping("/me")
  public Map<String, Object> me(@AuthenticationPrincipal final Jwt jwt) {
    log.info("me: isCompromised() bei Passwort 'pass1234': {}", passwordChecker.check("pass1234").isCompromised());

    return Map.of(
      "subject", jwt.getSubject(),
      "claims", jwt.getClaims()
    );
  }

  @PostMapping("login")
  public ResponseEntity<TokenDTO> login(@RequestBody final LoginDTO login) {
    final var username = login.username();
    final var password = login.password();
    final TokenDTO result = keycloakService.login(username, password);

    if (result == null) {
      throw new UnauthorizedException("Benutzername oder Passwort sind falsch.");
    }
    return ResponseEntity.ok(result);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException ex) {
    return ResponseEntity.status(UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(UNAUTHORIZED)
  void onUnauthorized(@SuppressWarnings("unused") final HttpClientErrorException.Unauthorized ex) {
  }
}
