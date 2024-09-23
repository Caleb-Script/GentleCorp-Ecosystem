package com.gentlecorp.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.StatusType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({
  "username", "lastName", "firstName", "email","phoneNumber", "subscribed", "tierLevel",
  "birthdate","customerState", "gender", "maritalStatus", "address", "contactOptionsType", "interests",
  "contacts"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class CustomerModel extends BaseCustomerModel<CustomerModel> {

  public CustomerModel(Customer customer) {
    super(customer);
  }
}
