package com.gentlecorp.invoice.repository;

import com.gentlecorp.invoice.model.dto.BalanceDTO;
import com.gentlecorp.invoice.model.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.IF_NONE_MATCH;

@HttpExchange("/account")
public interface AccountRepository {
  @GetExchange("/{id}")
  Account getById(@PathVariable String id, @RequestHeader(AUTHORIZATION) String authorization);

  @GetExchange("/{id}")
  ResponseEntity<Account> getById(
    @PathVariable String id,
    @RequestHeader(IF_NONE_MATCH) String version,
    @RequestHeader(AUTHORIZATION) Jwt authorization
  );

  @GetExchange("/{id}/balance")
  ResponseEntity<BigDecimal> getBalanceById(
    @PathVariable String id,
//    @RequestHeader(IF_NONE_MATCH) String version,
    @RequestHeader(AUTHORIZATION) String authorization
  );

  @PatchExchange("/{id}/balance")
  ResponseEntity<Void> updateBalance(
    @PathVariable final String id,
    @RequestBody final BalanceDTO balanceDTO,
    @RequestHeader(AUTHORIZATION) String authorization
  );
}
