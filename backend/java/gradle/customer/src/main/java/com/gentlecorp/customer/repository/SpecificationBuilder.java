package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.exception.IllegalArgumentException;
import com.gentlecorp.customer.model.entity.Address;
import com.gentlecorp.customer.model.entity.Address_;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.entity.Customer_;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.StatusType;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class SpecificationBuilder {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private Specification<Customer> getCustomerSpecification(Collection<String> collection, Stream<Specification<Customer>> specificationStream) {
    return collection == null || collection.isEmpty() ? null :
      specificationStream.filter(Objects::nonNull)
        .reduce(Specification::and)
        .orElse(null);
  }

  private Specification<Customer> likeIgnoreCase(final SingularAttribute <Customer, String> attributeName, final String value) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(attributeName)),
      builder.lower(builder.literal(String.format("%%%s%%", value)))
    );
  }

  private Specification<Customer> likeIgnoreCaseInAddress(final SingularAttribute <Address, String> attributeName, final String value) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(attributeName)),
      builder.lower(builder.literal(String.format("%%%s%%", value)))
    );
  }

  public Optional<Specification<Customer>> build(final Map<String, ? extends List<String>> queryParams) {
    log.debug("build: queryParams={}", queryParams);

    if (queryParams.isEmpty()) {
      // No search criteria provided
      return Optional.empty();
    }

    final var specs = queryParams
      .entrySet()
      .stream()
      .map(this::toSpecification)
      .toList();

    if (specs.isEmpty() || specs.contains(null)) {
      return Optional.empty();
    }

    return Optional.of(Specification.allOf(specs));
  }

  private Specification<Customer> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();
    if ("contactOptions".contentEquals(key)) {
      return toSpecificationContactOptions(values);
    }

    if ("interests".contentEquals(key)) {
      return toSpecificationInterest(values);
    }

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
      case "lastName" -> lastName(value);
      case "prefix" -> prefix(value);
      case "email" -> email(value);
      case "isSubscribed" -> isSubscribed(value);
      case "tierLevel" -> tierLevel(value);
      case "birthdate" -> birthdate(value);
      case "gender" -> gender(value);
      case "maritalStatus" -> maritalStatus(value);
      case "customerStatus" -> customerStatus(value);
      case "username" -> username(value);
      case "zipCode" -> zipCode(value);
      case "city" -> city(value);
      case "state" -> state(value);
      case "country" -> country(value);
      default -> throw new IllegalArgumentException(key);
    };
  }

  private Specification<Customer> toSpecificationContactOptions(final Collection<String> options) {
    log.trace("build: contactOptions={}", options);
    return getCustomerSpecification(options, options.stream()
      .map(this::contactOptions));
  }

  private Specification<Customer> toSpecificationInterest(final Collection<String> interests) {
    log.trace("build: interests={}", interests);
    return getCustomerSpecification(interests, interests.stream()
      .map(this::interests));
  }

  private Specification<Customer> prefix(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.lastName)),
      builder.lower(builder.literal(String.format("%s%%", prefix)))
    );
  }

  private Specification<Customer> lastName(final String teil) {
    return likeIgnoreCase(Customer_.lastName, teil);
  }

  private Specification<Customer> username(final String teil) {
    return likeIgnoreCase(Customer_.username, teil);
  }

  private Specification<Customer> email(final String teil) {
    return likeIgnoreCase(Customer_.email, teil);
  }

  @SuppressWarnings({"CatchParameterName", "LocalFinalVariableName"})
  private Specification<Customer> tierLevel(final String tierLevel) {
    try {
      final int tierLevelInt = Integer.parseInt(tierLevel);
      return (root, _, builder) -> builder.equal(root.get(Customer_.tierLevel), tierLevelInt);
    } catch (final NumberFormatException ex) {
      log.warn("Invalid tierLevel format: {}", tierLevel, ex);
      return null;
    }
  }

  private Specification<Customer> isSubscribed(final String isSubscribed) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.isSubscribed),
      Boolean.parseBoolean(isSubscribed)
    );
  }

  private Specification<Customer> gender(final String geschlecht) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.gender),
      GenderType.of(geschlecht)
    );
  }

  private Specification<Customer> maritalStatus(final String familienstand) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.maritalStatus),
      MaritalStatusType.of(familienstand)
    );
  }

  private Specification<Customer> customerStatus(final String status) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.customerState),
     StatusType.of(status)
    );
  }

  private Specification<Customer> contactOptions(final String option) {
    final var contactOptionsType = ContactOptionsType.of(option);
    if (contactOptionsType == null) {
      return null;
    }
    return (root, query, builder) -> builder.like(
      root.get(Customer_.contactOptionsString),
      builder.literal(String.format("%%%s%%", contactOptionsType.name()))
    );
  }

  private Specification<Customer> interests(final String option) {
    final var interestsType = InterestType.of(option);
    if (interestsType == null) {
      return null;
    }
    return (root, query, builder) -> builder.like(
      root.get(Customer_.interestsString),
      builder.literal(String.format("%%%s%%", interestsType.name()))
    );
  }

  private Specification<Customer> zipCode(final String prefix) {
    return likeIgnoreCaseInAddress(Address_.zipCode, prefix);
  }

  private Specification<Customer> city(final String prefix) {
    return likeIgnoreCaseInAddress(Address_.city, prefix);
  }

  private Specification<Customer> state(final String prefix) {
    return likeIgnoreCaseInAddress(Address_.state, prefix);
  }

  private Specification<Customer> country(final String prefix) {
    return likeIgnoreCaseInAddress(Address_.country, prefix);
  }

  private Specification<Customer> birthdate(final String value) {
    log.trace("birthdate: birthdate={}", value);
    LocalDate date;
    String[] parts = value.split(";");

    try {
      if (parts.length == 3) {
        // Fall für "between"
        LocalDate startDate = LocalDate.parse(parts[1].trim(), DATE_FORMATTER);
        LocalDate endDate = LocalDate.parse(parts[2].trim(), DATE_FORMATTER);
        return (root, query, builder) -> builder.between(root.get(Customer_.birthdate), startDate, endDate);
      } else if (parts.length == 2) {
        date = LocalDate.parse(parts[1].trim(), DATE_FORMATTER);
        if (value.startsWith("before")) {
          return (root, query, builder) -> builder.lessThanOrEqualTo(root.get(Customer_.birthdate), date);
        } else if (value.startsWith("after")) {
          return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(Customer_.birthdate), date);
        }
      }
    } catch (Exception ex) {
      log.warn("Invalid birthdate format: {}", value, ex);
    }

    // Rückgabe von null für ungültige Filterparameter
    return null;
  }
}
