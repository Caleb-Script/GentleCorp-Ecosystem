package com.gentle.bank.customer.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gentle.bank.customer.entity.Address;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.entity.enums.ContactOptionsType;
import com.gentle.bank.customer.entity.enums.GenderType;
import com.gentle.bank.customer.entity.enums.MaritalStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a model for the {@link Customer} entity, used for HATEOAS responses.
 * <p>
 * This class is a representation of a customer, including fields such as name, email, contact options, and address.
 * It extends {@link RepresentationModel} to support HATEOAS, which allows the inclusion of hypermedia links.
 * </p>
 * <p>
 * The {@link CustomerModel} class is used to transfer customer data over HTTP responses in a RESTful API,
 * providing a structured view of customer information.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@JsonPropertyOrder({
  "username", "lastName", "firstName", "email", "contactOptionsType",
  "isElite", "birthDate", "gender", "maritalStatus", "address"
})
@Relation(collectionRelation = "customers", itemRelation = "customer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@ToString(callSuper = true)
public class CustomerModel extends RepresentationModel<CustomerModel> {

  /**
   * The last name of the customer.
   */
  private final String lastName;

  /**
   * The first name of the customer.
   */
  private final String firstName;

  /**
   * The email address of the customer.
   * This field is used for identification and is included in the equality and hash code calculations.
   */
  @EqualsAndHashCode.Include
  private final String email;

  /**
   * The username of the customer.
   */
  private final String username;

  /**
   * Indicates if the customer has an elite status.
   */
  private final boolean isElite;

  /**
   * The birth date of the customer.
   */
  private final LocalDate birthDate;

  /**
   * The gender of the customer.
   */
  private final GenderType gender;

  /**
   * The marital status of the customer.
   */
  private final MaritalStatusType maritalStatus;

  /**
   * The address associated with the customer.
   */
  private final Address address;

  /**
   * The contact options preferred by the customer.
   */
  private final List<ContactOptionsType> contactOptionsType;

  /**
   * Constructs a new {@link CustomerModel} from a given {@link Customer} entity.
   * <p>
   * This constructor initializes the fields of {@link CustomerModel} using the provided {@link Customer}
   * entity, mapping entity attributes to model fields.
   * </p>
   *
   * @param customer the {@link Customer} entity to convert into a {@link CustomerModel}.
   */
  public CustomerModel(final Customer customer) {
    lastName = customer.getLastName();
    firstName = customer.getFirstName();
    email = customer.getEmail();
    username = customer.getUsername();
    isElite = customer.isElite();
    birthDate = customer.getBirthDate();
    gender = customer.getGender();
    maritalStatus = customer.getMaritalStatus();
    address = customer.getAddress();
    contactOptionsType = customer.getContactOptions();
  }
}
