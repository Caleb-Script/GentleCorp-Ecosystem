package com.gentle.bank.customer.service;

import com.gentle.bank.customer.KeycloakProps;
import com.gentle.bank.customer.dto.TokenDTO;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.exception.SignUpException;
import com.gentle.bank.customer.repository.KeycloakRepository;
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
 * Service class for user login and registration in Keycloak.
 * <p>
 * This service handles authentication and registration of users in Keycloak, including token management, user information retrieval,
 * and role assignment. It provides methods to log in users, register new users, and update user information.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakService {

  private final KeycloakRepository keycloakRepository;
  private final KeycloakProps keycloakProps;
  private String clientAndSecretEncoded;
  private final JwtService jwtService;

  /**
   * Initializes and encodes the client ID and secret for authentication with Keycloak.
   * <p>
   * This method is called after the construction of the service to ensure that the client ID and secret are encoded and ready for use.
   * </p>
   */
  @PostConstruct
  private void encodeClientAndSecret() {
    final var clientAndSecret = keycloakProps.clientId() + ':' + keycloakProps.clientSecret();
    clientAndSecretEncoded = Base64
      .getEncoder()
      .encodeToString(clientAndSecret.getBytes(Charset.defaultCharset()));
  }

  /**
   * Logs in a user with the given username and password.
   *
   * @param username the username of the user.
   * @param password the password of the user.
   * @return a {@link TokenDTO} containing the access token and other information.
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

  /**
   * Retrieves the master token using client credentials.
   *
   * @return the master token.
   */
  private String getMasterToken() {
    log.debug("getMasterToken");
    final var masterToken = keycloakRepository.masterToken(
      "grant_type=client_credentials&client_id=admin-cli&client_secret=ifChPS97cOFZCSWPgSqHSaCMY3QgNY7x",
      APPLICATION_FORM_URLENCODED_VALUE
    );
    log.debug("masterToken: " + masterToken);
    return masterToken.accessToken();
  }

  /**
   * Retrieves an admin token for performing administrative tasks.
   *
   * @return the admin token.
   */
  private String getAdminToken() {
    log.debug("getAdminToken");
    final var adminToken = login("admin", "p");
    final var accessToken = adminToken.accessToken();
    log.debug("accessToken: " + accessToken);
    return accessToken;
  }

  /**
   * Retrieves user information based on the provided token.
   *
   * @param token the access token for user information retrieval.
   * @return the user ID from the token information.
   */
  private String getUserInfo(final String token) {
    log.debug("getUserInfo: token={}", token);

    final var info = keycloakRepository.userInfo("Bearer " + token, APPLICATION_FORM_URLENCODED_VALUE);
    log.debug("info: " + info);
    final var id = info.sub();
    log.debug("id: " + id);
    return id;
  }

  /**
   * Registers a new user in Keycloak.
   *
   * @param customer the {@link Customer} object containing user details.
   * @param password the password for the new user.
   * @param role the role to be assigned to the user.
   * @throws SignUpException if there is an error during registration.
   */
  public void signIn(final Customer customer, final String password, final String role) {
    log.debug("signIn: customer={}", customer);

    // JSON data for registration
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
      password // Ensure password is present in Customer object
    );

    log.debug("signIn: customerData={}", customerData);

    try {
      // Register user in Keycloak and get user ID
      final var response = keycloakRepository.signIn(
        customerData,
        "Bearer " + getMasterToken(),
        APPLICATION_JSON_VALUE
      );
      log.info("signIn: Customer registered in Keycloak");

      final var accessToken = login(customer.getUsername(), password).accessToken();
      final var userId = getUserInfo(accessToken);
      log.debug("signIn: userId={}", userId);

      // Assign role to user
      assignRoleToUser(userId, role);

    } catch (Exception e) {
      log.error("Error during user registration: ", e);
      throw new SignUpException("User registration failed: " + e.getMessage());
    }
  }

  /**
   * Assigns a role to a user in Keycloak.
   *
   * @param userId the ID of the user in Keycloak.
   * @param roleName the name of the role to be assigned.
   */
  private void assignRoleToUser(String userId, String roleName) {
    log.debug("Assigning role {} to user {}", roleName, userId);

    final var token = getAdminToken();
    final var roleId = getRole(roleName, token);

    // JSON data for role assignment
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

  /**
   * Retrieves the role ID based on the role name.
   *
   * @param roleName the name of the role.
   * @param token the token for authorization.
   * @return the ID of the role.
   */
  private String getRole(final String roleName, final String token) {
    log.debug("getRole: roleName={}, token={}", roleName, token);

    final var roles = keycloakRepository.getRoles("Bearer " + token, APPLICATION_JSON_VALUE);
    log.debug("getRole: roles={}", roles);

    final var role = roles.stream()
      .filter(r -> r.name().equals(roleName)).findFirst().orElse(null);

    if (role == null) {
      throw new RuntimeException("RoleDTO not found: " + roleName);
    }
    log.debug("getRole: role={}", role);
    return role.id();
  }

  /**
   * Updates user information in Keycloak.
   *
   * @param customer the {@link Customer} object containing updated user details.
   * @param jwt the JWT containing the access token for authorization.
   */
  public void update(final Customer customer, final Jwt jwt) {
    log.debug("update: customer={}", customer);

    // Retrieve user ID based on access token
    final var userId = jwtService.getUserID(jwt);

    // JSON data for user update
    final var userData = """
          {
            "firstName": "%s",
            "lastName": "%s",
            "email": "%s",
            "username": "%s",
            "enabled": true
          }
          """.formatted(
      customer.getFirstName(),
      customer.getLastName(),
      customer.getEmail(),
      customer.getUsername()
    );
    log.debug("update: userData={}", userData);

    try {
      // Call repository to update user in Keycloak
      keycloakRepository.updateUser(
        userData,
        "Bearer " + getMasterToken(),
        APPLICATION_JSON_VALUE,
        userId
      );
    } catch (Exception e) {
      log.error("Error updating user: ", e);
      throw new RuntimeException("Failed to update user: " + e.getMessage());
    }
  }

  /**
   * Updates the password for a user in Keycloak.
   *
   * @param newPassword the new password to be set.
   * @param jwt the JWT containing the access token for authorization.
   */
  public void updatePassword(String newPassword, final Jwt jwt) {

    final var userId = jwtService.getUserID(jwt);

    final var passwordData = """
          {
            "type": "password",
            "value": "%s",
            "temporary": false
          }
          """.formatted(newPassword);

    log.debug("updatePassword: passwordData={}", passwordData);

    try {
      keycloakRepository.updateUserPassword(
        passwordData,
        "Bearer " + getMasterToken(),
        APPLICATION_JSON_VALUE,
        userId
      );
    } catch (Exception e) {
      log.error("Error updating password for user {}: ", userId, e);
      throw new RuntimeException("Failed to update password for user: " + e.getMessage());
    }
  }
}
