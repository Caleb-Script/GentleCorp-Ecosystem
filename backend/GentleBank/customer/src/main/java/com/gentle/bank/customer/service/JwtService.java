
package com.gentle.bank.customer.service;

import com.gentle.bank.customer.entity.enums.Role;
import com.gentle.bank.customer.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentle.bank.customer.entity.enums.Role.ELITE;
import static com.gentle.bank.customer.entity.enums.Role.GENTLEBANK_ADMIN;
import static com.gentle.bank.customer.entity.enums.Role.GENTLEBANK_USER;
import static com.gentle.bank.customer.entity.enums.Role.GENTLECORP_ADMIN;
import static com.gentle.bank.customer.entity.enums.Role.GENTLECORP_USER;

/**
 * Service-Klasse, um Benutzernamen und Rollen aus einem JWT von Keycloak zu extrahieren.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Service
@Slf4j
@SuppressWarnings("java:S5852")
public class JwtService {
    /**
     * Zu einem gegebenen JWT wird der zugehörige Username gesucht.
     *
     * @param jwt JWT für Security
     * @return Der gesuchte Username oder null
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

    public String getUserID(final Jwt jwt) {
      log.debug("getUserID");
      if (jwt == null) {
        throw new NotFoundException();
      }
      final var id = (String) jwt.getClaims().get("sub");
      log.debug("getUserID: id={}", id);
      return id;
    }

    public String getRole(final Jwt jwt) {
      final var realmRoles = getRealmRole(jwt);
      log.debug("getRole: realmRoles={}", realmRoles);

      if(realmRoles.contains(GENTLECORP_ADMIN)) {
        final var clientRoles = getClientRole(jwt);
        if(clientRoles.contains(GENTLEBANK_ADMIN)) {
          return "ADMIN";
        } else {
          return "USER";
        }
      } else if(realmRoles.contains(GENTLECORP_USER)) {
        final var clientRoles = getClientRole(jwt);
        if(clientRoles.contains(GENTLEBANK_USER)) {
          return "USER";
        } else {
          return "ELITE";
        }
      } else if( realmRoles.contains(ELITE)) {
        return "ELITE";
      } else {
        return "CUSTOMER";
      }
    }

    /**
     * Zu einem gegebenen JWT werden die zugehörigen Rollen gesucht.
     *
     * @param jwt JWT für Security
     * @return Die gesuchten Rollen oder die leere Liste
     */
    public List<Role> getRealmRole(final Jwt jwt) {
        @SuppressWarnings("unchecked")
        final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
        final var rollenStr = realmAccess.get("roles");
        log.trace("getRollen: rollenStr={}", rollenStr);
        return rollenStr
            .stream()
            .map(Role::of)
            .filter(Objects::nonNull)
            .toList();
    }

  public List<Role> getClientRole(final Jwt jwt) {
    @SuppressWarnings("unchecked")
    final var resourceAccess = (Map<String, Map<String, List<String>>>) jwt.getClaims().get("resource_access");
    final var client = resourceAccess.get("GentleBank");
    final var rollenStr = client.get("roles");
    log.trace("getRollen: rollenStr={}", rollenStr);
    return rollenStr
      .stream()
      .map(Role::of)
      .filter(Objects::nonNull)
      .toList();
  }
}
