package com.gentle.bank.customer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing an address associated with a customer.
 * <p>
 * This class defines the address entity with attributes such as street, house number, zip code, city, state, and country.
 * It is mapped to the "address" table in the database.
 * </p>
 *
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
@Entity
@Table(name = "address")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Address {

  /**
   * Unique identifier for the address.
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Street of the address.
   */
  private String street;

  /**
   * House number of the address.
   */
  private String houseNumber;

  /**
   * Zip code of the address.
   */
  private String zipCode;

  /**
   * City of the address.
   */
  private String city;

  /**
   * State of the address.
   */
  private String state;

  /**
   * Country of the address.
   */
  private String country;
}
