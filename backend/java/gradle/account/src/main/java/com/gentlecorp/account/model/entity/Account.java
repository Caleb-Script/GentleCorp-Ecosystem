package com.gentlecorp.account.model.entity;

import com.gentlecorp.account.model.enums.AccountType;
import com.gentlecorp.account.model.enums.StatusType;
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
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Account {
  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;

  @Enumerated(EnumType.STRING)
  private AccountType category;

  private BigDecimal balance;

  private int rateOfInterest;

  private int overdraft;

  private int withdrawalLimit;

  private UUID customerId;

  @Enumerated(EnumType.STRING)
  private StatusType state;

  @CreationTimestamp
  private LocalDateTime created;

  @UpdateTimestamp
  private LocalDateTime updated;

  @Transient
  private String customerUsername;
}
