package com.gentle.bank.customer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for creating a new customer, which includes customer details and password information.
 * <p>
 * This record is used when a new customer is being created in the system. It contains:
 * <ul>
 *     <li>{@link CustomerDTO} - Details of the customer to be created.</li>
 *     <li>{@link PasswordDTO} - Password for the new customer.</li>
 * </ul>
 * </p>
 *
 * @param customerDTO The details of the customer to be created.
 * @param passwordDTO The password for the new customer.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record CustomerCreateDTO(
  /**
   * The details of the customer to be created. This includes all relevant customer information.
   * <p>
   * The customer details are represented by the {@link CustomerDTO} class.
   * </p>
   */
  @JsonProperty("customer")
  CustomerDTO customerDTO,

  /**
   * The password for the new customer. This is required to set up the customer's account.
   * <p>
   * The password information is represented by the {@link PasswordDTO} class.
   * </p>
   */
  @JsonProperty("password")
  PasswordDTO passwordDTO
) {
}
