
package com.gentle.bank.customer.security;

/**
 * JSON-Datensatz von Keycloak zum Einloggen mit Benutzername und Passwort.
 *
 * @param username Benutzername
 * @param password Passwort
 */
public record LoginDTO(String username, String password) {
}
