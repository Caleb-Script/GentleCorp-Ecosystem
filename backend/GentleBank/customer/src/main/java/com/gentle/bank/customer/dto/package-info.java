/**
 * Contains Data Transfer Objects (DTOs) used throughout the application for data exchange.
 * <p>
 * This package includes various DTOs that encapsulate the data structures used for communication
 * between different layers of the application or with external systems. DTOs in this package include:
 * <ul>
 *   <li>{@link com.gentle.bank.customer.dto.CustomerDTO} - Represents customer details.</li>
 *   <li>{@link com.gentle.bank.customer.dto.AddressDTO} - Represents address information.</li>
 *   <li>{@link com.gentle.bank.customer.dto.CustomerCreateDTO} - Used for creating new customers, including details and password.</li>
 *   <li>{@link com.gentle.bank.customer.dto.PasswordDTO} - Represents a password value.</li>
 *   <li>{@link com.gentle.bank.customer.dto.LoginDTO} - Represents login credentials with username and password.</li>
 *   <li>{@link com.gentle.bank.customer.dto.RoleDTO} - Represents role information retrieved from Keycloak.</li>
 *   <li>{@link com.gentle.bank.customer.dto.TokenDTO} - Represents authentication tokens from Keycloak.</li>
 *   <li>{@link com.gentle.bank.customer.dto.UserInfoDTO} - Represents user information retrieved from Keycloak's userinfo endpoint.</li>
 * </ul>
 * </p>
 *
 * <p>DTOs are used for transferring data in a structured and consistent format, ensuring
 * that data is properly validated and transformed as it moves through the system.</p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
package com.gentle.bank.customer.dto;
