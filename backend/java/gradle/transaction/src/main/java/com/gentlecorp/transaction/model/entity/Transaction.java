package com.gentlecorp.transaction.model.entity;

import com.gentlecorp.transaction.model.enums.TransactionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Transaction {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;

  @Transient
  private TransactionType type;

  private BigDecimal amount;

  private UUID sender;
  private UUID receiver;

  @CreationTimestamp
  private LocalDateTime created;
}
