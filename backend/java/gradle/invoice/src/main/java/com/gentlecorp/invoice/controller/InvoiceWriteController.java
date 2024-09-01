package com.gentlecorp.invoice.controller;

import com.gentlecorp.invoice.exception.ConstraintViolationsException;
import com.gentlecorp.invoice.model.dto.InvoiceDTO;
import com.gentlecorp.invoice.model.dto.PaymentDTO;
import com.gentlecorp.invoice.model.mapper.InvoiceMapper;
import com.gentlecorp.invoice.service.InvoiceWriteService;
import com.gentlecorp.invoice.service.JwtService;
import com.gentlecorp.invoice.util.UriHelper;
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

import static com.gentlecorp.invoice.util.Constants.INVOICE_PATH;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.status;

@Controller
@RequestMapping(INVOICE_PATH)
@RequiredArgsConstructor
@Slf4j
public class InvoiceWriteController {

  private final InvoiceWriteService invoiceWriteService;
  private final JwtService jwtService;
  private final Validator validator;
  private final InvoiceMapper invoiceMapper;
  private final UriHelper uriHelper;


  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
    @RequestBody final InvoiceDTO invoiceDTO,
    final HttpServletRequest request
  ) throws URISyntaxException {
    log.debug("POST: invoiceDTO={}", invoiceDTO);
    final var violations = validator.validate(invoiceDTO, Default.class, InvoiceDTO.OnCreate.class);

    if (!violations.isEmpty()) {
      log.debug("create: violations={}", violations);
      throw new ConstraintViolationsException(violations);
    }

    final var invoiceInput = invoiceMapper.toInvoice(invoiceDTO);
    final var invoice = invoiceWriteService.create(invoiceInput);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), invoice.getId()));
    return created(location).build();
  }

  @PostMapping(path = "pay")
  public ResponseEntity<Void> pay(
    @RequestBody final PaymentDTO paymentDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
    ) throws URISyntaxException {
    log.debug("PAY: paymentDTO={}", paymentDTO);
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: customerUsername={}", username);

    if (username == null) {
      log.error("Despite Spring Security, getById() was called without a customerUsername in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var role = jwtService.getRole(jwt);
    if (role == null) {
      log.error("Despite Spring Security, getRole() was called without a Role in the JWT");
      return status(UNAUTHORIZED).build();
    }
    final var token = "Bearer " + jwt.getTokenValue();
    final var payment = invoiceMapper.toPayment(paymentDTO);
    log.debug("pay: payment={}", payment);
    final var payments = invoiceWriteService.pay(paymentDTO.invoiceId(), payment, username, role, token);
    final var recentPayment = payments.getLast();
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), recentPayment.getId()));
    return created(location).build();

  }
}
