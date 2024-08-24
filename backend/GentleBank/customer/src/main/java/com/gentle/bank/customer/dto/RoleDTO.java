package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) for representing a role obtained from a Keycloak response.
 * <p>
 * This DTO is used to encapsulate the details of a realm role received from Keycloak, including its identifier and name.
 * It is used for transferring role information between different layers of the application, particularly in requests
 * and responses involving role data obtained from Keycloak.
 * </p>
 *
 * <p>
 * This DTO encapsulates the details of a role as returned by Keycloak when querying the endpoint:
 * <code>http://localhost:8880/realms/GentleCorp-Ecosystem/admin/realms/GentleCorp-Ecosystem/roles</code>.
 * </p>
 *
 * @param id The unique identifier of the role as returned by Keycloak.
 * @param name The name of the role as returned by Keycloak.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record RoleDTO(
  /**
   * The unique identifier of the role.
   * <p>
   * This field represents the role's ID as provided by Keycloak and is used to uniquely identify a specific role within
   * the Keycloak realm.
   * </p>
   */
  @JsonProperty("id")
  String id,

  /**
   * The name of the role.
   * <p>
   * This field represents the role's name as provided by Keycloak, offering a human-readable description of the role.
   * </p>
   */
  @JsonProperty("name")
  String name
) {
}
