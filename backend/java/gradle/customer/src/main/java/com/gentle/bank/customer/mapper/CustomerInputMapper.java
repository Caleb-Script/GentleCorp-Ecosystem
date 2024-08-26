package com.gentle.bank.customer.mapper;

import com.gentle.bank.customer.dto.AddressDTO;
import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.entity.Address;
import com.gentle.bank.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting between {@link CustomerDTO} and {@link Customer},
 * as well as between {@link AddressDTO} and {@link Address}.
 * <p>
 * This interface uses MapStruct to automatically generate implementations for the mapping methods.
 * The mappings facilitate the transformation of data between the domain entities and their corresponding
 * data transfer objects (DTOs).
 * </p>
 *
 * <p>Example usage:
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
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerInputMapper {

  /**
   * Converts a {@link CustomerDTO} to a {@link Customer}.
   *
   * @param customerDTO the data transfer object representing the customer.
   * @return the corresponding {@link Customer} entity.
   */
  Customer toCustomer(CustomerDTO customerDTO);

  /**
   * Converts an {@link AddressDTO} to an {@link Address}.
   *
   * @param addressDTO the data transfer object representing the address.
   * @return the corresponding {@link Address} entity.
   */
  Address toAddress(AddressDTO addressDTO);

}
