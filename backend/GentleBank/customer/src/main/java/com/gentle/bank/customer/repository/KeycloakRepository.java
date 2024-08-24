package com.gentle.bank.customer.repository;

import com.gentle.bank.customer.dto.RoleDTO;
import com.gentle.bank.customer.dto.TokenDTO;
import com.gentle.bank.customer.dto.UserInfoDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Repository interface for interacting with Keycloak via HTTP.
 * <p>
 * This interface defines methods for communicating with Keycloak, a popular open-source identity and access management tool.
 * It includes endpoints for obtaining configuration, logging in, managing user roles, and more.
 * The methods use Spring Web Service annotations to specify the HTTP operations (GET, POST, PUT) and their respective URIs.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@HttpExchange
public interface KeycloakRepository {

  /**
   * Retrieves the Keycloak OpenID configuration.
   * <p>
   * This method sends a GET request to the OpenID configuration endpoint of Keycloak to obtain configuration details.
   * </p>
   *
   * @return a map containing the OpenID configuration details.
   */
  @GetExchange("http://localhost:8880/realms/GentleCorp-Ecosystem/.well-known/openid-configuration")
  Map<String, Object> openidConfiguration();

  /**
   * Logs in to Keycloak and retrieves an access token, refresh token, and expiration details.
   * <p>
   * This method sends a POST request to the token endpoint of Keycloak with the provided login data and headers to obtain
   * JWT tokens for authentication.
   * </p>
   *
   * @param loginData the login credentials as a URL-encoded string.
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request body (application/x-www-form-urlencoded).
   * @return a {@link TokenDTO} containing the JWT tokens and their expiration details.
   * @throws HttpClientErrorException.Unauthorized if authentication fails with status code 401.
   */
  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/token")
  @SuppressWarnings("JavadocReference")
  TokenDTO login(
    @RequestBody String loginData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  /**
   * Signs in a user to Keycloak by creating a new user.
   * <p>
   * This method sends a POST request to create a new user in Keycloak. The request body must contain the user details.
   * </p>
   *
   * @param customer the customer details in JSON format.
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request body (application/json).
   * @return an {@link HttpResponse} indicating the result of the operation.
   */
  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users")
  @SuppressWarnings("JavadocReference")
  HttpResponse<Void> signIn(
    @RequestBody String customer,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  /**
   * Retrieves an access token from Keycloak's master realm.
   * <p>
   * This method sends a POST request to obtain an access token from Keycloak's master realm, typically used for administrative tasks.
   * </p>
   *
   * @param adminData the admin credentials in URL-encoded format.
   * @param contentType the content type of the request body (application/x-www-form-urlencoded).
   * @return a {@link TokenDTO} containing the JWT token.
   */
  @PostExchange("/realms/master/protocol/openid-connect/token")
  @SuppressWarnings("JavadocReference")
  TokenDTO masterToken(
    @RequestBody String adminData,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  /**
   * Assigns a role to a user in Keycloak.
   * <p>
   * This method sends a POST request to assign a role to a user in Keycloak. The request body must contain role assignment data.
   * </p>
   *
   * @param roleData the role assignment details in JSON format.
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request body (application/json).
   * @param userId the ID of the user to whom the role is assigned.
   */
  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/role-mappings/realm")
  @SuppressWarnings("JavadocReference")
  void assignRoleToUser(
    @RequestBody String roleData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  /**
   * Retrieves user information from Keycloak.
   * <p>
   * This method sends a POST request to the userinfo endpoint to obtain information about the currently authenticated user.
   * </p>
   *
   * @param authorization the Bearer token for authentication.
   * @param contentType the content type of the request (application/x-www-form-urlencoded).
   * @return a {@link UserInfoDTO} containing the user's information.
   */
  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/userinfo")
  @SuppressWarnings("JavadocReference")
  UserInfoDTO userInfo(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  /**
   * Retrieves a list of roles from Keycloak.
   * <p>
   * This method sends a GET request to obtain all roles available in the specified realm of Keycloak.
   * </p>
   *
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request (application/json).
   * @return a collection of {@link RoleDTO} representing the roles in Keycloak.
   */
  @GetExchange("/admin/realms/GentleCorp-Ecosystem/roles")
  @SuppressWarnings("JavadocReference")
  Collection<RoleDTO> getRoles(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  /**
   * Updates user information in Keycloak.
   * <p>
   * This method sends a PUT request to update the information of an existing user in Keycloak.
   * </p>
   *
   * @param userData the updated user details in JSON format.
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request body (application/json).
   * @param userId the ID of the user to be updated.
   */
  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}")
  @SuppressWarnings("JavadocReference")
  void updateUser(
    @RequestBody String userData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  /**
   * Updates a user's password in Keycloak.
   * <p>
   * This method sends a PUT request to reset a user's password in Keycloak.
   * </p>
   *
   * @param passwordData the new password in JSON format.
   * @param authorization the Base64-encoded credentials for BASIC authentication.
   * @param contentType the content type of the request body (application/json).
   * @param userId the ID of the user whose password is to be reset.
   */
  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/reset-password")
  @SuppressWarnings("JavadocReference")
  void updateUserPassword(
    @RequestBody String passwordData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );
}
