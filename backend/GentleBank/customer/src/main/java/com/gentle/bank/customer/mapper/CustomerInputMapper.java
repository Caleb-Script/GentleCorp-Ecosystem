package com.gentle.bank.customer.mapper;

import com.gentle.bank.customer.dto.AddressDTO;
import com.gentle.bank.customer.dto.CustomerDTO;
import com.gentle.bank.customer.entity.Address;
import com.gentle.bank.customer.entity.Customer;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerInputMapper {
    Customer toCustomer(CustomerDTO customerDTO);

    Address toAddress(AddressDTO addressDTO);

}
