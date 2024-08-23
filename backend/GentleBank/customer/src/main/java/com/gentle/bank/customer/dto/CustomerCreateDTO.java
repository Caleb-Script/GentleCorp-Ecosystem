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
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
public record CustomerCreateDTO(
  @JsonProperty("customer")
  CustomerDTO customerDTO,

  @JsonProperty("password")
  PasswordDTO passwordDTO
) {
}
