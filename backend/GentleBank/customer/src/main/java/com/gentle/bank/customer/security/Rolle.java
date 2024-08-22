package com.gentle.bank.customer.security;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Singleton für verfügbare Rollen als Strings für das Spring-Interface GrantedAuthority.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@RequiredArgsConstructor
public enum Rolle {
  /**
   * Die Rolle ADMIN.
   */
  GENTLECORP_ADMIN("gentlecorp-admin"),
  GENTLEBANK_ADMIN("gentlebank-admin"),
  ESSENTIAL("gentlecorp-essential"),
  ELITE("gentlecorp-elite"),

  /**
   * Die Rolle USER.
   */
  GENTLECORP_USER("gentlecorp-user"),
  GENTLEBANK_USER("gentlebank-user");
  public static final String ROLE_PREFIX = "ROLE_";
  private final String role;

  /**
   * Zu einem String die Rolle als Enum ermitteln.
   *
   * @param str String einer Rolle
   * @return Rolle als Enum oder null
   */
  public static Rolle of(final String str) {
    return Stream.of(values())
      .filter(rolle -> rolle.role.equalsIgnoreCase(str))
      .findFirst()
      .orElse(null);
  }

  @JsonValue
  public String getRole() {
    return role;
  }
}
