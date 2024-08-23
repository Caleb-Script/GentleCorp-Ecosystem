package com.gentle.bank.customer.keycloak;

import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.security.KeycloakRepository;
import com.gentle.bank.customer.security.TokenDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Service-Klasse für das Login und die Registrierung von Benutzern in Keycloak.
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
   * Methode zum Anmelden eines Benutzers.
   *
   * @param username Benutzername des Benutzers.
   * @param password Passwort des Benutzers.
   * @return TokenDTO mit Access-Token und weiteren Informationen.
   */
    public TokenDTO login(final String username, final String password) {
        log.debug("login: username={}, password={}", username, password);
        log.debug("KEYCLOAK PROPS: clientID={}, clientSecret={}", keycloakProps.clientId(), keycloakProps.clientSecret());

        final var tokenDTO = keycloakRepository.login(
          "grant_type=password&username=" + username
            + "&password=" + password
            + "&client_id=" + keycloakProps.clientId()
            + "&client_secret=" + keycloakProps.clientSecret()
            + "&scope=openid",
            "Basic " + clientAndSecretEncoded,
            APPLICATION_FORM_URLENCODED_VALUE
        );

        log.debug("token: " + tokenDTO);
        return tokenDTO;
    }

    private String getMasterToken() {
      log.debug("getMasterToken");
      final var masterToken = keycloakRepository.masterToken(
        "grant_type=client_credentials&client_id=admin-cli&client_secret=ifChPS97cOFZCSWPgSqHSaCMY3QgNY7x",
        APPLICATION_FORM_URLENCODED_VALUE
      );
      log.debug("masterToken: " + masterToken);
      return masterToken.accessToken();
    }

  private String getAdminToken() {
    log.debug("getAdminToken");
    final var adminToken = login("admin", "p");
    final var accessToken = adminToken.accessToken();
    log.debug("accessToken: " + accessToken);
    return accessToken;
  }

  private String getUserInfo(final String token) {
    log.debug("getUserInfo: token={}", token);

    final var info = keycloakRepository.userInfo("Bearer "+ token,APPLICATION_FORM_URLENCODED_VALUE);
    log.debug("info: " + info);
    final var id = info.sub();
    log.debug("id: " + id);
    return id;
  }
  /**
   * Methode zum Registrieren eines neuen Benutzers in Keycloak.
   *
   * @param customer Customer-Objekt mit den Benutzerdetails.
   */
  public void signIn(final Customer customer, final String password, final String role, final Jwt jwt) {
    log.debug("signIn: customer={}", customer);

    // JSON-Daten für die Registrierung vorbereiten
    final var customerData = """
            {
                "username": "%s",
                "enabled": true,
                "firstName": "%s",
                "lastName": "%s",
                "email": "%s",
                "credentials": [{
                    "type": "password",
                    "value": "%s",
                    "temporary": false
                }]
            }
            """.formatted(
      customer.getUsername(),
      customer.getFirstName(),
      customer.getLastName(),
      customer.getEmail(),
      password // Sicherstellen, dass das Passwort im Customer-Objekt vorhanden ist
    );

    log.debug("signIn: customerData={}", customerData);

    try {
      final var masterToken = getMasterToken();
      // Benutzer in Keycloak registrieren und Benutzer-ID erhalten
      final var response = keycloakRepository.signIn(
        customerData,
        "Bearer " + masterToken,
        APPLICATION_JSON_VALUE
      );

      final var userAccessToken = login(customer.getUsername(), password).accessToken();
      final var userId = getUserInfo(userAccessToken);
      log.debug("signIn: userId={}", userId);

      // Rolle "gentlebank-customer" zuweisen
      assignRoleToUser(userId, role);

    } catch (Exception e) {
      log.error("Error during user registration: ", e);
      throw new RuntimeException("User registration failed: " + e.getMessage());
    }
  }

  /**
   * Methode, um eine Rolle zu einem Benutzer zuzuweisen.
   *
   * @param userId Die ID des Benutzers in Keycloak.
   * @param roleName Der Name der Rolle, die zugewiesen werden soll.
   */
  private void assignRoleToUser(String userId, String roleName) {
    log.debug("Assigning role {} to user {}", roleName, userId);

    final var token = getAdminToken();
    final var roleId = getRole(roleName, token);

    // JSON-Daten für die Rollenzuweisung vorbereiten
    final var roleData = """
            [{
                "id": "%s",
                "name": "%s"
            }]
            """.formatted(roleId, roleName);

    log.debug("roleData={}", roleData);
    try {
      keycloakRepository.assignRoleToUser(
        roleData,
        "Bearer " + getMasterToken(),
        APPLICATION_JSON_VALUE,
        userId
      );

    } catch (Exception e) {
      log.error("Error assigning role to user: ", e);
      throw new RuntimeException("Failed to assign role to user: " + e.getMessage());
    }
  }

  private String getRole(final String roleName, final String token) {
    log.debug("getRole: roleName={}, token={}", roleName, token);

    final var roles = keycloakRepository.getRoles("Bearer " + token, APPLICATION_JSON_VALUE);
    log.debug("getRole: roles={}", roles);

    final var role = roles.stream()
      .filter(r -> r.name().equals(roleName)).findFirst().orElse(null);

    if (role == null) {
      throw new RuntimeException("Role not found: " + roleName);
    }
    log.debug("getRole: role={}", role);
    return role.id();
  }
}
