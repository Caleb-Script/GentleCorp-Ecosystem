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
  "birthDate","customerState", "gender", "maritalStatus", "address", "contactOptionsType", "interests",
  "contacts"
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
  private final String phoneNumber;
  private final String username;
  private final int tierLevel;
  private final boolean isSubscribed;
  private final LocalDate birthDate;
  private final StatusType customerState;
  private final GenderType gender;
  private final MaritalStatusType maritalStatus;
  private final AddressModel address;

  private final List<ContactOptionsType> contactOptionsType;
  private final List<InterestType> interests;

//  private final List<AccountModel> accounts;
//  private final List<ContactModel> contacts;

  public CustomerModel(final Customer customer) {
    lastName           = customer.getLastName();
    firstName          = customer.getFirstName();
    email              = customer.getEmail();
    phoneNumber        = customer.getPhoneNumber();
    username           = customer.getUsername();

    tierLevel          = customer.getTierLevel();

    isSubscribed       = customer.isSubscribed();

    birthDate          = customer.getBirthDate();
    gender             = customer.getGender();
    maritalStatus      = customer.getMaritalStatus();
    customerState      = customer.getCustomer_state();

    address            = new AddressModel(customer.getAddress());

    contactOptionsType = customer.getContactOptions();
    interests          = customer.getInterests();

//    accounts           = customer.getAccounts().stream().map(AccountModel::new).toList();
//    contacts           = customer.getContacts().stream().map(ContactModel::new).toList();
  }
}
