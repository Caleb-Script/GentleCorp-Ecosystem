package com.gentle.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Address {
    @Id
    @GeneratedValue
    private UUID id;

    private String street;

    private String houseNumber;

    private String zipCode;

  private String state;

    private String city;
}
