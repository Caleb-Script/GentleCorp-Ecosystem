package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) for representing user information retrieved from Keycloak's
 * /userinfo endpoint.
 * <p>
 * This DTO is used to encapsulate the details of a user's identity information as returned by
 * Keycloak when querying the endpoint: <code>http://localhost:8880/realms/GentleCorp-Ecosystem/protocol/openid-connect/userinfo</code>.
 * </p>
 *
 * @param sub The unique identifier of the user, typically used to retrieve user-specific data or perform
 *             authentication and authorization checks.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record UserInfoDTO(
  @JsonProperty("sub")
  String sub
) {
}

