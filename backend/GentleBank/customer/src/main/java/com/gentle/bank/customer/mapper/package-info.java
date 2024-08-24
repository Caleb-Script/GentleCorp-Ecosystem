/**
 * Contains the MapStruct mappers for converting between domain objects and Data Transfer Objects (DTOs).
 * <p>
 * This package includes interfaces for mapping between {@link com.gentle.bank.customer.entity.Customer} and
 * {@link com.gentle.bank.customer.dto.CustomerDTO}, as well as between
 * {@link com.gentle.bank.customer.entity.Address} and {@link com.gentle.bank.customer.dto.AddressDTO}.
 * </p>
 * <p>
 * Mappers in this package facilitate the conversion of data between different layers of the application,
 * such as between entities used in persistence and DTOs used for transferring data between processes or over
 * the network.
 * </p>
 * <p>Example:
 * <pre>{@code
 * CustomerDTO customerDTO = ...;
 * Customer customer = customerInputMapper.toCustomer(customerDTO);
 * }</pre>
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
package com.gentle.bank.customer.mapper;
