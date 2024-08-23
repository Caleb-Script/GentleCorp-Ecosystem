package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gentle.bank.customer.entity.enums.ScopeType;
import com.gentle.bank.customer.entity.enums.TokenType;

/**
 * Data Transfer Object (DTO) representing a JSON response from Keycloak after a successful login
 * with a username and password.
 * <p>
 * This DTO encapsulates the details of the authentication tokens provided by Keycloak,
 * including access tokens, refresh tokens, token type, and other relevant information
 * necessary for OAuth2 authentication and session management.
 * </p>
 *
 * @param accessToken The access token for OAuth2. This token is used to access protected resources.
 * @param expiresIn The number of seconds until the access token expires.
 * @param refreshExpiresIn The number of seconds until the refresh token expires.
 * @param refreshToken The refresh token for OAuth2. This token is used to obtain a new access token.
 * @param tokenType The type of the token, typically "Bearer" for OAuth2.
 * @param notBeforePolicy A timestamp indicating when the token can be used, to prevent misuse.
 * @param sessionState The UUID of the session within which access tokens and refresh tokens can be requested.
 * @param idToken The ID token provided by Keycloak, which contains user information.
 * @param scope The scope of the access granted by the token, as per <a href="https://www.rfc-editor.org/rfc/rfc6749.html">OAuth 2.0</a> (e.g., "email profile").
 *
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
public record TokenDTO(
  @JsonProperty("access_token")
  String accessToken,

  @JsonProperty("expires_in")
  int expiresIn,

  @JsonProperty("refresh_expires_in")
  int refreshExpiresIn,

  @JsonProperty("refresh_token")
  String refreshToken,

  @JsonProperty("token_type")
  TokenType tokenType,

  @JsonProperty("not-before-policy")
  int notBeforePolicy,

  @JsonProperty("session_state")
  String sessionState,

  @JsonProperty("id_token")
  String idToken,

  ScopeType scope
) {
}
