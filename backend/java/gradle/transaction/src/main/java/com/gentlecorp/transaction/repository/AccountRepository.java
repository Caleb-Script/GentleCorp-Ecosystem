package com.gentlecorp.transaction.repository;

import com.gentlecorp.transaction.model.entity.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

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
        @RequestHeader(AUTHORIZATION)  String authorization
    );
}
