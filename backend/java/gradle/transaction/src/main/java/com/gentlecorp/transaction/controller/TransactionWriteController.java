package com.gentlecorp.transaction.controller;

import com.gentlecorp.transaction.exception.ConstraintViolationsException;
import com.gentlecorp.transaction.model.dto.TransactionDTO;
import com.gentlecorp.transaction.model.mapper.TransactionMapper;
import com.gentlecorp.transaction.service.TransactionWriteService;
import com.gentlecorp.transaction.service.JwtService;
import com.gentlecorp.transaction.util.UriHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.URISyntaxException;

import static com.gentlecorp.transaction.util.Constants.TRANSACTION_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@Controller
@RequestMapping(TRANSACTION_PATH)
@RequiredArgsConstructor
@Slf4j
public class TransactionWriteController {

  private final TransactionWriteService transactionWriteService;
  private final Validator validator;
  private final TransactionMapper transactionMapper;
  private final UriHelper uriHelper;


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
    @RequestBody final TransactionDTO transactionDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
  ) throws URISyntaxException {
    log.debug("POST: transactionDTO={}", transactionDTO);
    final var violations = validator.validate(transactionDTO, Default.class, TransactionDTO.OnCreate.class);

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);
      throw new ConstraintViolationsException(violations);
    }

    final var transactionInput = transactionMapper.toTransaction(transactionDTO);
    final var transaction = transactionWriteService.create(transactionInput,jwt);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), transaction.getId()));
    return created(location).build();
  }
}
