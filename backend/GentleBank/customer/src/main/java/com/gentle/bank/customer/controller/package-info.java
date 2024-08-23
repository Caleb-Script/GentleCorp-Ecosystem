/**
 * This package contains the controllers responsible for handling HTTP requests related to customer operations in the Gentle Bank application.
 * <p>
 * The controllers in this package manage the creation, retrieval, update, and deletion of customer records, as well as handling specific
 * actions such as password updates. These controllers interact with the service layer to perform business logic and use DTOs (Data Transfer Objects)
 * to exchange data between the client and server.
 * </p>
 * <p>
 * Security is enforced through JWT (JSON Web Token) authentication, ensuring that only authorized users can access or modify customer data.
 * Each controller also handles various exceptions, returning appropriate HTTP status codes and detailed error messages conforming to the
 * RFC 7807 specification.
 * </p>
 * <p>
 * Key classes in this package include:
 * <ul>
 *     <li>{@link com.gentle.bank.customer.controller.CustomerWriteController}: Handles write operations such as creating, updating, and deleting customers.</li>
 *     <li>{@link com.gentle.bank.customer.controller.CustomerReadController}: Handles read operations such as retrieving customer details.</li>
 * </ul>
 * </p>
 * <p>
 * This package works closely with the service layer (found in {@link com.gentle.bank.customer.service}) and utilizes mappers (found in
 * {@link com.gentle.bank.customer.mapper}) to convert between entity objects and DTOs.
 * </p>
 *
 * @since 23.08.2024
 * @author Caleb Gyamfi
 * @see com.gentle.bank.customer.service
 * @see com.gentle.bank.customer.dto
 * @see com.gentle.bank.customer.security
 */
package com.gentle.bank.customer.controller;
