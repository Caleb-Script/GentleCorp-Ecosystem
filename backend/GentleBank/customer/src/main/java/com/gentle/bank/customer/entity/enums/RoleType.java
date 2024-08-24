package com.gentle.bank.customer.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Enum representing available roles for the Spring {@link org.springframework.security.core.GrantedAuthority} interface.
 * <p>
 * This enum defines various role types as strings that can be used to specify roles for users in the system.
 * Each enum constant represents a specific role with a string value that corresponds to the role name.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@RequiredArgsConstructor
public enum RoleType {
  /**
   * Role for administrative access within GentleCorp.
   */
  GENTLECORP_ADMIN("gentlecorp-admin"),

  /**
   * Role for administrative access within GentleBank.
   */
  GENTLEBANK_ADMIN("gentlebank-admin"),

  /**
   * Role for essential customers within GentleCorp.
   */
  ESSENTIAL("gentlecorp-essential"),

  /**
   * Role for elite customers within GentleCorp.
   */
  ELITE("gentlecorp-elite"),

  /**
   * Role for general users within GentleCorp.
   */
  GENTLECORP_USER("gentlecorp-user"),

  /**
   * Role for general users within GentleBank.
   */
  GENTLEBANK_USER("gentlebank-user");

  /** Prefix used for roles in Spring Security. */
  public static final String ROLE_PREFIX = "ROLE_";

  /** The string representation of the role. */
  private final String role;

  /**
   * Finds the {@link RoleType} enum constant associated with the given string value.
   * <p>
   * This method is used to convert a string representation of a role into its corresponding enum constant.
   * For example, it can convert the string "gentlecorp-admin" to the {@link RoleType#GENTLECORP_ADMIN} enum constant.
   * </p>
   *
   * @param str The string value representing the role type.
   * @return The {@link RoleType} enum constant associated with the given string value, or {@code null} if no match is found.
   */
  public static RoleType of(final String str) {
    return Stream.of(values())
      .filter(role -> role.role.equalsIgnoreCase(str))
      .findFirst()
      .orElse(null);
  }

  /**
   * Returns the string representation of the role.
   * <p>
   * This method returns the string value associated with the role type. For example, it returns "gentlecorp-admin"
   * for the {@link RoleType#GENTLECORP_ADMIN} enum constant.
   * </p>
   *
   * @return The string representation of the role.
   */
  @JsonValue
  public String getRole() {
    return role;
  }
}
