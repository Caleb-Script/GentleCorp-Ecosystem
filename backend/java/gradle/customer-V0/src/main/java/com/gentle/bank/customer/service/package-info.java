/**
 * This package contains services related to customer operations within the Gentle Bank system.
 * <p>
 * Services in this package handle various business logic aspects of customer management,
 * including creating, updating, and deleting customer records, as well as integrating with
 * external systems for authentication and email notifications.
 * </p>
 * <p>
 * Classes in this package include:
 * </p>
 * <ul>
 *   <li>{@link com.gentle.bank.customer.service.CustomerReadService} - Provides read operations and querying capabilities for customer data.</li>
 *   <li>{@link com.gentle.bank.customer.service.CustomerWriteService} - Manages write operations, including creating, updating, and deleting customers, and handles email notifications and Keycloak integrations.</li>
 *   <li>{@link com.gentle.bank.customer.service.KeycloakService} - Handles interactions with Keycloak for user authentication and authorization, including login, registration, and role management.</li>
 *   <li>{@link com.gentle.bank.customer.service.JwtService} - Extracts user information and roles from JWT tokens for security and authorization purposes.</li>
 *   <li>{@link com.gentle.bank.customer.service.MailService} - Responsible for sending emails related to customer activities and notifications.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
package com.gentle.bank.customer.service;
