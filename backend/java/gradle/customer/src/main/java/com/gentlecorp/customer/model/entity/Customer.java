package com.gentlecorp.customer.model.entity;

import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.StatusType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.interfaces.VersionedEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

@Entity
@Table(name = "customer")
@NamedEntityGraph(name = Customer.ADDRESS_GRAPH, attributeNodes = @NamedAttributeNode("address"))
@NamedEntityGraph(name = Customer.ALL_GRAPH, attributeNodes = {
  @NamedAttributeNode("address"), @NamedAttributeNode("contacts")
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Customer implements VersionedEntity {

  public static final String ADDRESS_GRAPH = "Customer.address";
  public static final String ALL_GRAPH = "Customer.all";

  @Id
  @GeneratedValue
  @EqualsAndHashCode.Include
  private UUID id;

  @Version
  private int version;
  private String lastName;
  private String firstName;
  private String email;
  private String phoneNumber;
  private String username;

  private int tierLevel;

  private boolean isSubscribed;

  private LocalDate birthDate;

  @Enumerated(STRING)
  private GenderType gender;

  @Enumerated(EnumType.STRING)
  private MaritalStatusType maritalStatus;

  @Enumerated(EnumType.STRING)
  private StatusType customer_state;

  @ToString.Exclude
  @OneToOne(fetch = LAZY, cascade = {PERSIST, REMOVE}, optional = false, orphanRemoval = true)
  private Address address;

  @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
  @JoinColumn(name = "customer_id")
  @OrderColumn(name = "idx", nullable = false)
  @ToString.Exclude
  private List<Contact> contacts;

  @CreationTimestamp
  private LocalDateTime created;
  @UpdateTimestamp
  private LocalDateTime updated;

  @Transient
  private List<InterestType> interests;
  @Column(name = "interests")
  private String interestsString;

  @Transient
  private List<ContactOptionsType> contactOptions;
  @Column(name = "contact_options")
  private String contactOptionsString;

  public void set(final Customer customer) {
    lastName = customer.getLastName();
    firstName = customer.getFirstName();
    email = customer.getEmail();
    phoneNumber = customer.getPhoneNumber();
    username = customer.getUsername();

    tierLevel = customer.getTierLevel();
    isSubscribed = customer.isSubscribed();

    birthDate = customer.getBirthDate();

    gender = customer.getGender();
    maritalStatus = customer.getMaritalStatus();
    customer_state = customer.getCustomer_state();
  }

  public void setInterestsString(final List<InterestType> interests) {
    final var interestsStringList = interests.stream()
      .map(Enum::name)
      .toList();
    this.interestsString = String.join(",", interestsStringList);
  }

  public void setContactOptionsString(final List<ContactOptionsType> optionsTypes) {
    final var contactOptionsStringList = optionsTypes.stream()
      .map(Enum::name)
      .toList();
    this.contactOptionsString = String.join(",", contactOptionsStringList);
  }

  @PrePersist
  private void buildInterestsStr() {
    if (interests == null || interests.isEmpty()) {
      interestsString = null;
    } else {
      final var stringList = interests.stream()
        .map(Enum::name)
        .toList();
      interestsString = String.join(",", stringList);
    }

    if (contactOptions == null || contactOptions.isEmpty()) {
      contactOptionsString = null;
      return;
    }
    final var stringList = contactOptions.stream()
      .map(Enum::name)
      .toList();
    contactOptionsString = String.join(",", stringList);
  }

  @PostLoad
  private void loadInterests() {
    if (interestsString == null) {
      interests = emptyList();
    } else {
      final var interestsArray = interestsString.split(",");
      interests = Arrays.stream(interestsArray)
        .map(InterestType::valueOf)
        .collect(Collectors.toList());
    }
    if (contactOptionsString == null) {
      contactOptions = emptyList();
    }
    final var contactOptionsArray = contactOptionsString.split(",");
    contactOptions = Arrays.stream(contactOptionsArray)
      .map(ContactOptionsType::valueOf)
      .collect(Collectors.toList());
  }
}
