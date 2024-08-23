
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
 * Repository für einen Spring-HTTP-Client für Keycloak.
 */
@HttpExchange
public interface KeycloakRepository {
    /**
     * GET-Request, um von Keycloak die Konfigurationsdaten abzufragen.
     *
     * @return Die Konfigurationsdaten als Map
     */
    @GetExchange("http://localhost:8880/realms/GentleCorp-Ecosystem/.well-known/openid-configuration")
    Map<String, Object> openidConfiguration();

    /**
     * POST-Request, um von Keycloak einen JSON-Datensatz mit Access-Token, Refresh-Token und Ablaufdauer zu erhalten.
     *
     * @param loginData als String für den Request-Body zum Content-Type application/x-www-form-urlencoded
     * @param authorization String mit Base64-Codierung für "BASIC Authentication"
     * @param contentType Content-Type "application/x-www-form-urlencoded"
     * @return JWT nach erfolgreichem Einloggen
     * @throws HttpClientErrorException.Unauthorized für den Statuscode 401
     */
    @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/token")
    @SuppressWarnings("JavadocReference")
    TokenDTO login(
        @RequestBody String loginData,
        @RequestHeader(AUTHORIZATION) String authorization,
        @RequestHeader(CONTENT_TYPE) String contentType
    );

  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users")
  @SuppressWarnings("JavadocReference")
  HttpResponse<Void> signIn(
    @RequestBody String customer,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("/realms/master/protocol/openid-connect/token")
  @SuppressWarnings("JavadocReference")
  TokenDTO masterToken(
    @RequestBody String adminData,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PostExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/role-mappings/realm")
  @SuppressWarnings("JavadocReference")
  void assignRoleToUser(
    @RequestBody String roleData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/userinfo")
  @SuppressWarnings("JavadocReference")
  UserInfoDTO userInfo(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @GetExchange("/admin/realms/GentleCorp-Ecosystem/roles")
  @SuppressWarnings("JavadocReference")
  Collection<RoleDTO> getRoles(
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType
  );

  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}")
  @SuppressWarnings("JavadocReference")
  void updateUser(
    @RequestBody String userData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );

  @PutExchange("/admin/realms/GentleCorp-Ecosystem/users/{userId}/reset-password")
  @SuppressWarnings("JavadocReference")
  void updateUserPassword(
    @RequestBody String passwordData,
    @RequestHeader(AUTHORIZATION) String authorization,
    @RequestHeader(CONTENT_TYPE) String contentType,
    @PathVariable("userId") String userId
  );


}
