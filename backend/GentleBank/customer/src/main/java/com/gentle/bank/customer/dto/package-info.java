/**
 * Contains Data Transfer Objects (DTOs) for the customer module of the Gentle Bank application.
 * <p>
 * DTOs in this package are used to encapsulate and transfer data between various layers of the application,
 * such as between controllers and services or between different services. They represent the data models
 * in a format suitable for communication, often including validation annotations and JSON property mappings.
 * </p>
 * <p>
 * Key classes in this package include:
 * <ul>
 *     <li>{@link com.gentle.bank.customer.dto.AddressDTO} - DTO for address information.</li>
 *     <li>{@link com.gentle.bank.customer.dto.CustomerDTO} - DTO for customer details.</li>
 *     <li>{@link com.gentle.bank.customer.dto.LoginDTO} - DTO for login credentials.</li>
 *     <li>{@link com.gentle.bank.customer.dto.PasswordDTO} - DTO for password information.</li>
 *     <li>{@link com.gentle.bank.customer.dto.TokenDTO} - DTO for authentication tokens from Keycloak.</li>
 *     <li>{@link com.gentle.bank.customer.dto.CustomerCreateDTO} - DTO for creating a new customer, including customer details and password.</li>
 * </ul>
 * </p>
 *
 * @since 23.08.2024
 * @version 1.0
 * @author Caleb Gyamfi
 */
package com.gentle.bank.customer.dto;
