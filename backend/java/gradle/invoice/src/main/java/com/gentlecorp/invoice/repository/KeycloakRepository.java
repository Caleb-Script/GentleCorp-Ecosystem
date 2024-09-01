package com.gentlecorp.invoice.repository;

import com.gentlecorp.invoice.model.dto.TokenDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@HttpExchange
public interface KeycloakRepository {
  @PostExchange("/realms/GentleCorp-Ecosystem/protocol/openid-connect/token")
  TokenDTO login(
    @RequestBody String loginData,
    @RequestHeader(CONTENT_TYPE) String contentType
  );
}
