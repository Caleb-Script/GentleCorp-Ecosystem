package com.gentlecorp.invoice.model.entity;

import com.gentlecorp.invoice.model.enums.StatusType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

@Entity
@Table(name = "invoice")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Invoice {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;

  @Enumerated(EnumType.STRING)
  private StatusType type;

  private LocalDate dueDate;

  private BigDecimal amount;

  @Transient
  private BigDecimal amountLeft;

  @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
  @JoinColumn(name = "invoice_id")
  @OrderColumn(name = "idx", nullable = false)
  @ToString.Exclude
  private List<Payment> payments;

  private UUID accountId;

  @CreationTimestamp
  private LocalDateTime created;

  @UpdateTimestamp
  private LocalDateTime modified;
}
