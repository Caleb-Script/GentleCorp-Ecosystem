package com.gentlecorp.invoice.service;

import com.gentlecorp.invoice.exception.InsufficientFundsException;
import com.gentlecorp.invoice.exception.InvoiceAlreadyPaidException;
import com.gentlecorp.invoice.exception.NotFoundException;
import com.gentlecorp.invoice.model.dto.TransactionDTO;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.repository.AccountRepository;
import com.gentlecorp.invoice.repository.InvoiceRepository;
import com.gentlecorp.invoice.util.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.gentlecorp.invoice.model.enums.StatusType.PAID;
import static com.gentlecorp.invoice.model.enums.StatusType.PENDING;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InvoiceWriteService {

  private final InvoiceRepository invoiceRepository;
  private final InvoiceReadService invoiceReadService;
  private final KafkaTemplate<String, TransactionDTO> kafkaTemplate;
  private final Validation validation;

  public Invoice create(final Invoice invoice) {
    log.debug("create: invoice={}", invoice);
    invoice.setType(PENDING);
    final var invoiceDb = invoiceRepository.save(invoice);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    log.debug("create: invoiceDb={}", invoiceDb);
    return invoiceDb;
  }

  //TODO transaction -> invoice ->

  public List<Payment> pay(final UUID invoiceId, final Payment payment, final Jwt jwt) {
    log.debug("pay: payment={}", payment);
    final var accessToken = String.format("Bearer %s", jwt.getTokenValue());
    final var invoice = invoiceReadService.findById(invoiceId, jwt);
    final var account = invoiceReadService.findAccountById(invoice.getAccountId(), accessToken, false).stream().findFirst().orElseThrow(NotFoundException::new);
    validation.validateCustomer(account.customerUsername(),jwt);
    final var currentBalance = invoiceReadService.findCurrentBalanceByAccountId(invoice.getAccountId(), accessToken);
    validation.validatePayment(invoice, payment, currentBalance);
    sendTransaction(invoice, payment);
    invoice.getPayments().add(payment);
    log.debug("pay: Payment processed successfully, updated payments: {}", invoice.getPayments());
    return invoice.getPayments();
  }

  private void sendTransaction(final Invoice invoice, final Payment payment) {
    log.debug("sendTransaction: invoice={}", invoice);
    final var transation = new TransactionDTO(
      payment.getAmount(),
      "EURO",
      invoice.getAccountId(),
      UUID.fromString("30000000-0000-0000-0000-000000000000")
    );
    kafkaTemplate.send("payment", transation);
    log.debug("sendTransaction: transaction={}", transation);
  }

  //TODO pay with another account
}
