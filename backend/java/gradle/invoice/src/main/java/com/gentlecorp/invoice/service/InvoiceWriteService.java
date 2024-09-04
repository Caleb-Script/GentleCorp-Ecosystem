package com.gentlecorp.invoice.service;

import com.gentlecorp.invoice.exception.NotFoundException;
import com.gentlecorp.invoice.model.dto.PaymentDTO;
import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.repository.AccountRepository;
import com.gentlecorp.invoice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final AccountRepository accountRepository;

  public Invoice create(final Invoice invoice) {
    log.debug("create: invoice={}", invoice);
    invoice.setType(PENDING);
    final var invoiceDb = invoiceRepository.save(invoice);
    log.trace("create: Thread-ID={}", Thread.currentThread().threadId());

    log.debug("create: invoiceDb={}", invoiceDb);
    return invoiceDb;
  }

  public List<Payment> pay(final UUID invoiceId, final Payment payment, final String username, final String role, String token) {
    log.debug("pay: payment={}", payment);

    final var invoice = invoiceReadService.findById(invoiceId, username, role, token);

    if (invoice.getAmountLeft().compareTo(BigDecimal.ZERO) == 0) {
      invoice.setType(PAID);
    }

    if (invoice.getType().equals(PAID)) {
      log.debug("pay: Invoice already paid");
      //TODO bessere exception
      throw new NotFoundException(UUID.fromString("Invoice already paid"));
    }


    final var amountLeft =  invoice.getAmountLeft();

    if (payment.getAmount().compareTo(amountLeft) > 0) {
      //TODO zuviel
      payment.setAmount(amountLeft);
    }
//TODO customize accountId
    final var currentBalance = invoiceReadService.findCurrentBalanceByAccountId(invoice.getAccountId(), token);
    if (currentBalance.compareTo(payment.getAmount()) < 0) {
      //TODO zu wenig geld vorhanden
      payment.setAmount(currentBalance);
    }

    deductFromCurrentBalance(invoice.getAccountId(),payment.getAmount().multiply(BigDecimal.valueOf(-1)), token);

    //TODO cuurent balance abziehen
    invoice.getPayments().add(payment);
    log.debug("pay: Payment processed successfully, updated payments: {}", invoice.getPayments());
    return invoice.getPayments();
  }

  public void deductFromCurrentBalance(final UUID id, final BigDecimal amount, final String token) {
    log.debug("deductFromCurrentBalance:id={} amount={}",id, amount);
    final var balanceDTO = new PaymentDTO(amount);
    accountRepository.updateBalance(id.toString(), balanceDTO, token, "\"0\"");
  }
}
