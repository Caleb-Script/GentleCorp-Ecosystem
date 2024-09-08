package com.gentlecorp.transaction.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.enums.TransactionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonPropertyOrder({ "type", "amount", "sender", "receiver", "created" })
@Relation(collectionRelation = "transactions", itemRelation = "transaction")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class TransactionModel extends RepresentationModel<TransactionModel> {
  private final BigDecimal amount;
  private final UUID sender;
  private final UUID receiver;
  private final TransactionType type;
  private final LocalDateTime created;

  public TransactionModel(final Transaction transaction) {
    this.amount = transaction.getAmount();
    this.sender = transaction.getSender();
    this.receiver = transaction.getReceiver();
    this.type = transaction.getType();
    this.created = transaction.getCreated();
  }
}
