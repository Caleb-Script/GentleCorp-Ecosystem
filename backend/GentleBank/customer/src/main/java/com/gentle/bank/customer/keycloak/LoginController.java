package com.gentle.bank.customer.keycloak;


import com.gentle.bank.customer.security.TokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Javadoc-Kommentar für die LoginController-Klasse.
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

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
    public ResponseEntity<TokenDTO> login(@RequestBody final Login login) {
        final var username = login.username();
        final var password = login.password();

        // Call LoginService for authentication
        final TokenDTO result = loginService.login(username, password);
        log.debug("Login successful for username: {}", username);

        if (result == null) {
            throw new UnauthorizedException("Benutzername oder Passwort sind falsch.");
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Login with username and password.
     * Supports application/x-www-form-urlencoded and application/json.
     *
     * @return ResponseEntity containing TokenDTO if successful (Status 200).
     *         Throws UnauthorizedException with Status 401 if authentication fails.
     *
     */
    @GetMapping("login")
    public String login() {

        return "Hallo!";
    }

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
}
