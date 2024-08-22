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

import java.net.URL;
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

    /** Nachname des Kunden */
    private String lastName;

    /** Vorname des Kunden */
    private String firstName;
    /** E-Mail-Adresse des Kunden */
    private String email;

  private String username;
  private String password;

    /** Geburtsdatum des Kunden */
    private LocalDate birthDate;


    /** Geschlecht des Kunden */
    @Enumerated(STRING)
    private GenderType gender;

    /** Familienstand des Kunden */
    @Enumerated(EnumType.STRING)
    private MaritalStatusType maritalStatus;


    /** Kontaktmöglichkeiten des Kunden */
    @NotNull(message = "kontak optionen dürfen nicht null sein")
    @Transient
    @UniqueElements
    private List<ContactOptionsType> contactOptions;
    @Column(name = "contact_options")
    private String contactOptionsString;

    /** Adresse des Kunden */
    @ToString.Exclude
    @OneToOne(fetch = LAZY,  cascade = {PERSIST, REMOVE}, optional = false, orphanRemoval = true)
    private Address address;

    /** Erstellungszeitpunkt des Kunden */
    @CreationTimestamp
    private LocalDateTime created;

    /** Aktualisierungszeitpunkt des Kunden */
    @UpdateTimestamp
    private LocalDateTime updated;

    /**
     * Überschreiben der Kundendaten.
     *
     * @param customer Neue Kundendaten.
     */
    public void set(final Customer customer) {
        lastName = customer.getLastName();
        firstName = customer.getFirstName();
        email = customer.getEmail();
        birthDate = customer.getBirthDate();
        gender = customer.getGender();
        maritalStatus = customer.getMaritalStatus();
    }

    public void setContactOptionsString(final List<ContactOptionsType> optionsTypes) {
        final var contactOptionsStringList = optionsTypes.stream()
                .map(Enum::name)
                .toList();
        this.contactOptionsString = String.join(",", contactOptionsStringList);
    }

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
