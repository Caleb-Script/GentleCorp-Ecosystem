package com.gentlecorp.invoice.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.invoice.model.entity.Payment;
import com.gentlecorp.invoice.model.enums.StatusType;
import com.gentlecorp.invoice.model.entity.Invoice;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@JsonPropertyOrder({
  "type", "amount", "sender", "receiver", "payments"
})
@Relation(collectionRelation = "invoices", itemRelation = "invoice")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class InvoiceModel extends RepresentationModel<InvoiceModel> {
  private final BigDecimal amount;
  private final LocalDate dueDate;
  private final UUID accountId;
  private final StatusType type;
  private final List<Payment> payments;

  public InvoiceModel(final Invoice invoice) {
    this.amount = invoice.getAmount();
    this.dueDate = invoice.getDueDate();
    this.accountId = invoice.getAccountId();
    this.type = invoice.getType();
    this.payments = invoice.getPayments();
  }
}
