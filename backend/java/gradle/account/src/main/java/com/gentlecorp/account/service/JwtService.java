package com.gentlecorp.account.service;

import com.gentlecorp.account.exception.NotFoundException;
import com.gentlecorp.account.model.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.gentlecorp.account.model.enums.RoleType.*;

@Service
@Slf4j
@SuppressWarnings("java:S5852")
public class JwtService {
  public String getUsername(final Jwt jwt) {
    if (jwt == null) {
      throw new NotFoundException();
    }
    final var username = (String) jwt.getClaims().get("preferred_username");
    log.debug("JwtService: username={}", username);
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
    log.debug("JwtService: realmRoles={}", realmRoles);

    if (realmRoles.contains(ADMIN)) {
      return "ADMIN";
    }

    if (realmRoles.contains(USER)) {
      return "USER";
    }

    if (realmRoles.contains(SUPREME)) {
      return "SUPREME";
    }

    if (realmRoles.contains(ELITE)) {
      return "ELITE";
    }

    return "BASIC";
  }

  public List<RoleType> getRealmRole(final Jwt jwt) {
    @SuppressWarnings("unchecked") final var realmAccess = (Map<String, List<String>>) jwt.getClaims().get("realm_access");
    final var rolesStr = realmAccess.get("roles");
    log.trace("JwtService:: rolesStr={}", rolesStr);
    return rolesStr
      .stream()
      .filter(role -> {
        return Stream.of(RoleType.values())
          .anyMatch(validRole -> validRole.name().equalsIgnoreCase(role));
      })
      .map(RoleType::valueOf)
      .toList();
  }
}

