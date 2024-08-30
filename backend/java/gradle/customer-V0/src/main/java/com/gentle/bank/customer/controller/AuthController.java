package com.gentle.bank.customer.controller;

import com.gentle.bank.customer.dto.LoginDTO;
import com.gentle.bank.customer.dto.TokenDTO;
import com.gentle.bank.customer.service.KeycloakService;
import com.gentle.bank.customer.exception.UnauthorizedException;
import com.gentle.bank.customer.repository.KeycloakRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Controller for handling authentication-related requests.
 * <p>
 * This controller provides endpoints for user authentication, including
 * logging in and retrieving JWT information for the authenticated user.
 * It integrates with Keycloak for managing authentication and uses Spring
 * Security to secure the endpoints.
 * </p>
 * <p>
 * The controller also handles exceptions related to unauthorized access
 * by providing custom responses for these scenarios.
 * </p>
 *
 * @see com.gentle.bank.customer.service.KeycloakService
 * @see com.gentle.bank.customer.dto.LoginDTO
 * @see com.gentle.bank.customer.dto.TokenDTO
 * @see com.gentle.bank.customer.exception.UnauthorizedException
 * @since 24.08.2024
 * @version 1.0
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
   * The path for authentication-related requests.
   */
  public static final String AUTH_PATH = "/auth";

  private final CompromisedPasswordChecker passwordChecker;
  private final KeycloakService keycloakService;

  /**
   * Retrieves information about the currently authenticated user.
   * <p>
   * This endpoint returns the JWT subject and claims associated with the authenticated user.
   * It also logs whether a sample password has been compromised using the {@link CompromisedPasswordChecker}.
   * </p>
   *
   * @param jwt the JWT of the authenticated user, injected by Spring Security
   * @return a map containing the subject and claims of the JWT
   * @see org.springframework.security.oauth2.jwt.Jwt
   * @since 24.08.2024
   * @version 1.0
   */
  @GetMapping("/me")
  @Operation(summary = "JWT bei OAuth 2.0 abfragen", tags = "Auth")
  @ApiResponse(responseCode = "200", description = "Eingeloggt")
  @ApiResponse(responseCode = "401", description = "Fehler bei Username oder Passwort")
  public Map<String, Object> me(@AuthenticationPrincipal final Jwt jwt) {
    log.info("me: isCompromised() bei Passwort 'pass1234': {}", passwordChecker.check("pass1234").isCompromised());

    return Map.of(
      "subject", jwt.getSubject(),
      "claims", jwt.getClaims()
    );
  }

  /**
   * Authenticates a user with the provided username and password.
   * <p>
   * This endpoint supports both {@code application/x-www-form-urlencoded} and {@code application/json} formats.
   * If authentication is successful, it returns a {@link TokenDTO} containing the JWT token.
   * </p>
   *
   * @param login the login credentials provided by the user
   * @return a {@link ResponseEntity} containing the {@link TokenDTO} if authentication is successful
   * @throws UnauthorizedException if the username or password is incorrect
   * @see com.gentle.bank.customer.dto.LoginDTO
   * @see com.gentle.bank.customer.dto.TokenDTO
   * @since 24.08.2024
   * @version 1.0
   */
  @PostMapping("login")
  public ResponseEntity<TokenDTO> login(@RequestBody final LoginDTO login) {
    final var username = login.username();
    final var password = login.password();

    final TokenDTO result = keycloakService.login(username, password);
    log.debug("Login successful for username: {}", username);

    if (result == null) {
      throw new UnauthorizedException("Benutzername oder Passwort sind falsch.");
    }

    return ResponseEntity.ok(result);
  }

  /**
   * Handles {@link UnauthorizedException} thrown during authentication.
   * <p>
   * This method returns a {@link ResponseEntity} with a status of {@code 401 Unauthorized}
   * and the error message from the exception.
   * </p>
   *
   * @param ex the {@link UnauthorizedException} thrown
   * @return a {@link ResponseEntity} with a status of {@code 401 Unauthorized} and the exception message
   * @see com.gentle.bank.customer.exception.UnauthorizedException
   * @since 24.08.2024
   * @version 1.0
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException ex) {
    return ResponseEntity.status(UNAUTHORIZED).body(ex.getMessage());
  }

  /**
   * Handles {@link HttpClientErrorException.Unauthorized} thrown during HTTP requests.
   * <p>
   * This method responds with a status of {@code 401 Unauthorized} when an unauthorized exception occurs.
   * </p>
   *
   * @param ex the {@link HttpClientErrorException.Unauthorized} thrown
   * @since 24.08.2024
   * @version 1.0
   */
  @ExceptionHandler
  @ResponseStatus(UNAUTHORIZED)
  void onUnauthorized(@SuppressWarnings("unused") final HttpClientErrorException.Unauthorized ex) {
  }
}
