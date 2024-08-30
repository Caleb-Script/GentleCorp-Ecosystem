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
 * <p>
 * The entity includes basic address information necessary for customer records and is mapped using Jakarta Persistence (JPA).
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
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
   * <p>
   * This field is the primary key for the address entity and is auto-generated.
   * </p>
   */
  @Id
  @GeneratedValue
  private UUID id;

  /**
   * Street of the address.
   * <p>
   * Represents the street portion of the address.
   * </p>
   */
  private String street;

  /**
   * House number of the address.
   * <p>
   * Represents the house number portion of the address.
   * </p>
   */
  private String houseNumber;

  /**
   * Zip code of the address.
   * <p>
   * Represents the postal code for the address.
   * </p>
   */
  private String zipCode;

  /**
   * City of the address.
   * <p>
   * Represents the city portion of the address.
   * </p>
   */
  private String city;

  /**
   * State of the address.
   * <p>
   * Represents the state or province of the address.
   * </p>
   */
  private String state;

  /**
   * Country of the address.
   * <p>
   * Represents the country where the address is located.
   * </p>
   */
  private String country;
}
