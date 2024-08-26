package com.gentle.bank.customer.repository;

import com.gentle.bank.customer.entity.*;
import com.gentle.bank.customer.entity.enums.*;
import com.gentle.bank.customer.exception.IllegalArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Builds JPA {@link Specification}s for querying {@link Customer} entities.
 * <p>
 * This component translates a map of query parameters into JPA {@link Specification}s. It supports various customer attributes
 * and their combinations to facilitate flexible querying against the {@link Customer} entity.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Component
@Slf4j
public class SpecificationBuilder {

  /**
   * Constructs a {@link Specification} based on the provided query parameters.
   * <p>
   * This method iterates over the query parameters, converting each into a corresponding {@link Specification}. It aggregates
   * these specifications using logical conjunction (AND) to form a complete query specification.
   * </p>
   *
   * @param queryParams a map of query parameters where the key is the parameter name and the value is a list of parameter values.
   * @return an {@link Optional} containing the constructed {@link Specification} if query parameters are present; otherwise, an empty {@link Optional}.
   */
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

  /**
   * Converts a map entry into a {@link Specification} based on the key and value.
   * <p>
   * This method determines the type of specification to create based on the entry's key and the values it contains.
   * </p>
   *
   * @param entry a map entry where the key is the query parameter name and the value is a list of parameter values.
   * @return a {@link Specification} corresponding to the provided key and values; or {@code null} if the entry cannot be processed.
   */
  private Specification<Customer> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();
    if ("contact".contentEquals(key)) {
      return toSpecificationContactOptions(values);
    }

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.get(0);
    return switch (key) {
      case "lastName" -> lastName(value);
      case "email" -> email(value);
      case "elite" -> isElite(value);
      case "gender" -> gender(value);
      case "maritalStatus" -> maritalStatus(value);
      case "zipCode" -> zipCode(value);
      case "city" -> city(value);
      case "state" -> state(value);
      case "country" -> country(value);
      default -> throw new IllegalArgumentException(key);
    };
  }

  /**
   * Builds a {@link Specification} based on contact options.
   * <p>
   * This method creates a specification that matches customers with any of the specified contact options.
   * </p>
   *
   * @param options a collection of contact options to match.
   * @return a {@link Specification} that matches customers with the specified contact options; or {@code null} if the options are invalid or empty.
   */
  private Specification<Customer> toSpecificationContactOptions(final Collection<String> options) {
    log.trace("build: contactOptions={}", options);
    if (options == null || options.isEmpty()) {
      return null;
    }

    final var specsImmutable = options.stream()
      .map(this::contactOptions)
      .toList();
    if (specsImmutable.isEmpty() || specsImmutable.contains(null)) {
      return null;
    }
    final List<Specification<Customer>> specs = new ArrayList<>(specsImmutable);
    final var first = specs.remove(0);
    return specs.stream().reduce(first, Specification::and);
  }

  /**
   * Creates a {@link Specification} for customers with a specific last name.
   * <p>
   * This method creates a specification that matches customers whose last name contains the specified value, case-insensitive.
   * </p>
   *
   * @param teil the last name fragment to match.
   * @return a {@link Specification} that matches customers with the specified last name fragment.
   */
  private Specification<Customer> lastName(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.lastName)),
      builder.lower(builder.literal("%" + teil + "%"))
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific email address.
   * <p>
   * This method creates a specification that matches customers whose email contains the specified value, case-insensitive.
   * </p>
   *
   * @param teil the email fragment to match.
   * @return a {@link Specification} that matches customers with the specified email fragment.
   */
  private Specification<Customer> email(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.email)),
      builder.lower(builder.literal("%" + teil + "%"))
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific elite status.
   * <p>
   * This method creates a specification that matches customers based on their elite status.
   * </p>
   *
   * @param isElite a string indicating whether the customer is elite or not.
   * @return a {@link Specification} that matches customers with the specified elite status.
   */
  private Specification<Customer> isElite(final String isElite) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.isElite),
      Boolean.parseBoolean(isElite)
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific gender.
   * <p>
   * This method creates a specification that matches customers based on their gender.
   * </p>
   *
   * @param geschlecht the gender to match.
   * @return a {@link Specification} that matches customers with the specified gender.
   */
  private Specification<Customer> gender(final String geschlecht) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.gender),
      GenderType.of(geschlecht)
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific marital status.
   * <p>
   * This method creates a specification that matches customers based on their marital status.
   * </p>
   *
   * @param familienstand the marital status to match.
   * @return a {@link Specification} that matches customers with the specified marital status.
   */
  private Specification<Customer> maritalStatus(final String familienstand) {
    return (root, query, builder) -> builder.equal(
      root.get(Customer_.maritalStatus),
      MaritalStatusType.of(familienstand)
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific contact option.
   * <p>
   * This method creates a specification that matches customers whose contact options contain the specified value.
   * </p>
   *
   * @param option the contact option to match.
   * @return a {@link Specification} that matches customers with the specified contact option; or {@code null} if the option is invalid.
   */
  private Specification<Customer> contactOptions(final String option) {
    final var contactOptionsType = ContactOptionsType.of(option);
    if (contactOptionsType == null) {
      return null;
    }
    return (root, query, builder) -> builder.like(
      root.get(Customer_.contactOptionsString),
      builder.literal("%" + contactOptionsType.name() + "%")
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific zip code prefix.
   * <p>
   * This method creates a specification that matches customers whose address zip code contains the specified prefix.
   * </p>
   *
   * @param prefix the zip code prefix to match.
   * @return a {@link Specification} that matches customers with the specified zip code prefix.
   */
  private Specification<Customer> zipCode(final String prefix) {
    return (root, query, builder) -> builder.like(
      root.get(Customer_.address)
        .get(Address_.zipCode), "%" + prefix + "%"
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific city prefix.
   * <p>
   * This method creates a specification that matches customers whose address city contains the specified prefix.
   * </p>
   *
   * @param prefix the city prefix to match.
   * @return a {@link Specification} that matches customers with the specified city prefix.
   */
  private Specification<Customer> city(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.city)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific state prefix.
   * <p>
   * This method creates a specification that matches customers whose address state contains the specified prefix.
   * </p>
   *
   * @param prefix the state prefix to match.
   * @return a {@link Specification} that matches customers with the specified state prefix.
   */
  private Specification<Customer> state(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.state)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }

  /**
   * Creates a {@link Specification} for customers with a specific country prefix.
   * <p>
   * This method creates a specification that matches customers whose address country contains the specified prefix.
   * </p>
   *
   * @param prefix the country prefix to match.
   * @return a {@link Specification} that matches customers with the specified country prefix.
   */
  private Specification<Customer> country(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.country)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }
}
