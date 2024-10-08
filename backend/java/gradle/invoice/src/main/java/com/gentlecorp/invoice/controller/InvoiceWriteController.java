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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static com.gentlecorp.invoice.util.Constants.ID_PATTERN;
import static com.gentlecorp.invoice.util.Constants.INVOICE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

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


  @PostMapping(path = "{accountId:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> post(
    @PathVariable UUID accountId,
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
    invoiceInput.setAccountId(accountId);
    final var invoice = invoiceWriteService.create(invoiceInput);
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), invoice.getId()));
    return created(location).build();
  }

  @PostMapping(path = "{invoiceId:" + ID_PATTERN + "}/pay")
  public ResponseEntity<Void> pay(
    @PathVariable final UUID invoiceId,
    @RequestBody final PaymentDTO paymentDTO,
    final HttpServletRequest request,
    @AuthenticationPrincipal final Jwt jwt
    ) throws URISyntaxException {
    log.debug("PAY: paymentDTO={}", paymentDTO);
    final var username = jwtService.getUsername(jwt);
    log.debug("getById: customerUsername={}", username);
    final var payment = invoiceMapper.toPayment(paymentDTO);
    log.debug("pay: payment={}", payment);
    final var payments = invoiceWriteService.pay(invoiceId, payment, jwt);
    final var recentPayment = payments.getLast();
    final var baseUri = uriHelper.getBaseUri(request);
    final var location = new URI(String.format("%s/%s", baseUri.toString(), recentPayment.getId()));
    return created(location).build();

  }
}
