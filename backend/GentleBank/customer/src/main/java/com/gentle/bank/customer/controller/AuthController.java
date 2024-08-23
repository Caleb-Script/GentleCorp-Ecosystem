
package com.gentle.bank.customer.controller;


import com.gentle.bank.customer.dto.LoginDTO;
import com.gentle.bank.customer.dto.TokenDTO;
import com.gentle.bank.customer.KeycloakProps;
import com.gentle.bank.customer.service.KeycloakService;
import com.gentle.bank.customer.exception.UnauthorizedException;
import com.gentle.bank.customer.service.JwtService;
import com.gentle.bank.customer.repository.KeycloakRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Controller für Abfragen bei Security.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@RestController
@RequestMapping(AuthController.AUTH_PATH)
@Tag(name = "Authentifizierung API")
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S1075")
public class AuthController {
    /**
     * Pfad für Authentifizierung.
     */
    public static final String AUTH_PATH = "/auth";

    private final KeycloakRepository keycloakRepository;
    private final CompromisedPasswordChecker passwordChecker;
  private final KeycloakService keycloakService;
  private final JwtService jwtService;

    private final KeycloakProps keycloakProps;

    private String clientAndSecretEncoded;

    @PostConstruct
    private void encodeClientAndSecret() {
        final var clientAndSecret = "spring-client:" + keycloakProps.clientSecret();
        clientAndSecretEncoded = Base64
            .getEncoder()
            .encodeToString(clientAndSecret.getBytes(Charset.defaultCharset()));
    }

    @GetMapping("/me")
    @Operation(summary = "JWT bei OAuth 2.0 abfragen", tags = "Auth")
    @ApiResponse(responseCode = "200", description = "Eingeloggt")
    @ApiResponse(responseCode = "401", description = "Fehler bei Username oder Passwort")
    Map<String, Object> me(@AuthenticationPrincipal final Jwt jwt) {
        // https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
        log.info("me: isCompromised() bei Passwort 'pass1234': {}", passwordChecker.check("pass1234").isCompromised());

        return Map.of(
            "subject", jwt.getSubject(),
            "claims", jwt.getClaims()
        );
    }

  /**
   * Login with username and password.
   * Supports application/x-www-form-urlencoded and application/json.
   *
   * @param login Request Body with username and password.
   * @return ResponseEntity containing TokenDTO if successful (Status 200).
   *         Throws UnauthorizedException with Status 401 if authentication fails.
   *
   */
  @PostMapping("login")
  public ResponseEntity<TokenDTO> login(@RequestBody final LoginDTO login) {
    final var username = login.username();
    final var password = login.password();

    // Call KeycloakService for authentication
    final TokenDTO result = keycloakService.login(username, password);
    log.debug("Login successful for username: {}", username);

    if (result == null) {
      throw new UnauthorizedException("Benutzername oder Passwort sind falsch.");
    }

    return ResponseEntity.ok(result);
  }


  //TODO catch unauthorize exception
  /**
   * Exception handler for UnauthorizedException.
   *
   * @param ex exception
   * @return ResponseEntity with status code 401 and error message.
   *
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

    @ExceptionHandler
    @ResponseStatus(UNAUTHORIZED)
    void onUnauthorized(@SuppressWarnings("unused") final HttpClientErrorException.Unauthorized ex) {
    }


}
