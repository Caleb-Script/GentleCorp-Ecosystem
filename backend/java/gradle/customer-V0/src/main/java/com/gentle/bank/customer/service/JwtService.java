package com.gentle.bank.customer.service;

import com.gentle.bank.customer.entity.enums.RoleType;
import com.gentle.bank.customer.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentle.bank.customer.entity.enums.RoleType.ELITE;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLEBANK_ADMIN;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLEBANK_USER;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLECORP_ADMIN;
import static com.gentle.bank.customer.entity.enums.RoleType.GENTLECORP_USER;

/**
 * Service class for extracting usernames and roles from a JWT issued by Keycloak.
 * <p>
 * This service provides methods to retrieve the username, user ID, and roles from a JWT token.
 * It also categorizes the user's role based on the presence of specific realm and client roles.
 * </p>
 *
 * <p>Methods provided by this service include:</p>
 * <ul>
 *   <li>{@link #getUsername(Jwt)} - Retrieves the username from the JWT.</li>
 *   <li>{@link #getUserID(Jwt)} - Retrieves the user ID from the JWT.</li>
 *   <li>{@link #getRole(Jwt)} - Determines the role of the user based on realm and client roles.</li>
 *   <li>{@link #getRealmRole(Jwt)} - Retrieves a list of realm roles from the JWT.</li>
 *   <li>{@link #getClientRole(Jwt)} - Retrieves a list of client roles from the JWT for a specific client.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@Slf4j
@SuppressWarnings("java:S5852")
public class JwtService {

  /**
   * Retrieves the username from the given JWT.
   *
   * @param jwt the JWT from which to extract the username.
   * @return the username, or {@code null} if not found.
   * @throws NotFoundException if the JWT is {@code null}.
   */
  public String getUsername(final Jwt jwt) {
    log.debug("getUsername");
    if (jwt == null) {
      throw new NotFoundException();
    }
    final var username = (String) jwt.getClaims().get("preferred_username");
    log.debug("getUsername: username={}", username);
    return username;
  }

  /**
   * Retrieves the user ID from the given JWT.
   *
   * @param jwt the JWT from which to extract the user ID.
   * @return the user ID.
   * @throws NotFoundException if the JWT is {@code null}.
   */
  public String getUserID(final Jwt jwt) {
    log.debug("getUserID");
    if (jwt == null) {
      throw new NotFoundException();
    }
    final var id = (String) jwt.getClaims().get("sub");
    log.debug("getUserID: id={}", id);
    return id;
  }

  /**
   * Determines the role of the user based on the realm and client roles in the JWT.
   *
   * @param jwt the JWT from which to determine the user's role.
   * @return the role of the user as a string.
   */
  public String getRole(final Jwt jwt) {
    final var realmRoles = getRealmRole(jwt);
    log.debug("getRole: realmRoles={}", realmRoles);

    if (realmRoles.contains(GENTLECORP_ADMIN)) {
      final var clientRoles = getClientRole(jwt);
      if (clientRoles.contains(GENTLEBANK_ADMIN)) {
        return "ADMIN";
      } else {
        return "USER";
      }
    } else if (realmRoles.contains(GENTLECORP_USER)) {
      final var clientRoles = getClientRole(jwt);
      if (clientRoles.contains(GENTLEBANK_USER)) {
        return "USER";
      } else {
        return "ELITE";
      }
    } else if (realmRoles.contains(ELITE)) {
      return "ELITE";
    } else {
      return "CUSTOMER";
    }
  }

  /**
   * Retrieves a list of realm roles from the given JWT.
   *
   * @param jwt the JWT from which to extract realm roles.
   * @return a list of realm roles, or an empty list if no roles are found.
   */
  public List<RoleType> getRealmRole(final Jwt jwt) {
    @SuppressWarnings("unchecked")
    final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
    final var rolesStr = realmAccess.get("roles");
    log.trace("getRealmRole: rolesStr={}", rolesStr);
    return rolesStr
      .stream()
      .map(RoleType::of)
      .filter(Objects::nonNull)
      .toList();
  }

  /**
   * Retrieves a list of client roles from the given JWT for the "GentleBank" client.
   *
   * @param jwt the JWT from which to extract client roles.
   * @return a list of client roles, or an empty list if no roles are found.
   */
  public List<RoleType> getClientRole(final Jwt jwt) {
    @SuppressWarnings("unchecked")
    final var resourceAccess = (Map<String, Map<String, List<String>>>) jwt.getClaims().get("resource_access");
    final var client = resourceAccess.get("GentleBank");
    final var rolesStr = client.get("roles");
    log.trace("getClientRole: rolesStr={}", rolesStr);
    return rolesStr
      .stream()
      .map(RoleType::of)
      .filter(Objects::nonNull)
      .toList();
  }
}
