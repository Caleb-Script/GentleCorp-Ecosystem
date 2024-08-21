package com.gentle.bank.customer.controller.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.bank.customer.entity.Address;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.entity.enums.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
        "lastName", "firstName", "email", "contactOptionsType",
        "birthDate", "gender",  "maritalStatus", "address"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class CustomerModel extends RepresentationModel<CustomerModel> {
 private final String lastName;
    private final String firstName;
    @EqualsAndHashCode.Include
    private final String email;
    private final LocalDate birthDate;
    private final GenderType gender;
    private final MaritalStatusType maritalStatus;
    private final Address address;
  private final List<ContactOptionsType> contactOptionsType;

    public CustomerModel(final Customer customer) {
        lastName = customer.getLastName();
        firstName = customer.getFirstName();
        email = customer.getEmail();
        birthDate = customer.getBirthDate();
        gender = customer.getGender();
        maritalStatus = customer.getMaritalStatus();
        address = customer.getAddress();
      contactOptionsType = customer.getContactOptions();
    }
}
