package com.gentle.bank.customer.entity;

import com.gentle.bank.customer.entity.enums.ContactOptionsType;
import com.gentle.bank.customer.entity.enums.GenderType;
import com.gentle.bank.customer.entity.enums.MaritalStatusType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Collections.emptyList;

/**
 * Entity representing a customer in the system.
 * <p>
 * This class defines the customer entity with various attributes such as name, email, birth date, gender, marital status,
 * contact options, and address. It also handles the persistence logic for contact options as a comma-separated string in the
 * database and manages the conversion between the string and the list of {@link ContactOptionsType}.
 * </p>
 * <p>
 * The entity is mapped to the "customer" table in the database and includes timestamps for creation and last update.
 * </p>
 * <p>
 * The entity uses Jakarta Persistence (JPA) annotations for mapping to the database, and Lombok annotations for boilerplate code
 * generation. The entity manages the lifecycle of customer data including its creation and updates.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Entity
@Table(name = "customer")
@NamedEntityGraph(name = Customer.ADDRESS_GRAPH, attributeNodes = @NamedAttributeNode("address"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Slf4j
public class Customer {

  public static final String ADDRESS_GRAPH = "Customer.address";
  public static final String ALL_GRAPH = "Customer.addressActivities";

  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;

  /**
   * The last name of the customer.
   */
  private String lastName;

  /**
   * The first name of the customer.
   */
  private String firstName;

  /**
   * The email address of the customer.
   */
  private String email;

  private String username;

  private boolean isElite;

  /**
   * The birth date of the customer.
   */
  private LocalDate birthDate;

  /**
   * The gender of the customer.
   */
  @Enumerated(STRING)
  private GenderType gender;

  /**
   * The marital status of the customer.
   */
  @Enumerated(EnumType.STRING)
  private MaritalStatusType maritalStatus;

  /**
   * Contact options of the customer.
   * <p>
   * This field is not persisted directly but used to manage a list of contact options.
   * </p>
   */
  @NotNull(message = "Contact options must not be null")
  @Transient
  @UniqueElements
  private List<ContactOptionsType> contactOptions;

  @Column(name = "contact_options")
  private String contactOptionsString;

  /**
   * The address of the customer.
   */
  @ToString.Exclude
  @OneToOne(fetch = LAZY, cascade = {PERSIST, REMOVE}, optional = false, orphanRemoval = true)
  private Address address;

  /**
   * The timestamp when the customer was created.
   */
  @CreationTimestamp
  private LocalDateTime created;

  /**
   * The timestamp when the customer was last updated.
   */
  @UpdateTimestamp
  private LocalDateTime updated;

  /**
   * Updates the customer information with the provided customer data.
   *
   * @param customer The new customer data.
   */
  public void set(final Customer customer) {
    lastName = customer.getLastName();
    firstName = customer.getFirstName();
    email = customer.getEmail();
    isElite = customer.isElite();
    birthDate = customer.getBirthDate();
    gender = customer.getGender();
    maritalStatus = customer.getMaritalStatus();
  }

  /**
   * Sets the contact options as a comma-separated string for persistence.
   *
   * @param optionsTypes The list of contact options to be converted to a string.
   */
  public void setContactOptionsString(final List<ContactOptionsType> optionsTypes) {
    final var contactOptionsStringList = optionsTypes.stream()
      .map(Enum::name)
      .toList();
    this.contactOptionsString = String.join(",", contactOptionsStringList);
  }

  /**
   * Converts the contact options string from the database to a list of {@link ContactOptionsType}.
   * This method is called before the entity is persisted.
   */
  @PrePersist
  private void buildInterestsStr() {
    if (contactOptions == null || contactOptions.isEmpty()) {
      contactOptionsString = null;
      return;
    }
    final var stringList = contactOptions.stream()
      .map(Enum::name)
      .toList();
    contactOptionsString = String.join(",", stringList);
  }

  /**
   * Converts the contact options string from the database to a list of {@link ContactOptionsType}.
   * This method is called after the entity is loaded from the database.
   */
  @PostLoad
  private void loadInterests() {
    if (contactOptionsString == null) {
      contactOptions = emptyList();
    }
    final var contactOptionsArray = contactOptionsString.split(",");
    contactOptions = Arrays.stream(contactOptionsArray)
      .map(ContactOptionsType::valueOf)
      .collect(Collectors.toList());
  }
}
