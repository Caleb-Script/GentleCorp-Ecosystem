package com.gentle.bank.customer.repository;

import com.gentle.bank.customer.entity.*;
import com.gentle.bank.customer.entity.enums.*;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class SpecificationBuilder {
  /**
   * Specification für eine Query mit Spring Data bauen.
   *
   * @param queryParams als MultiValueMap
   * @return Specification für eine Query mit Spring Data
   */
  public Optional<Specification<Customer>> build(final Map<String, ? extends List<String>> queryParams) {
    log.debug("build: queryParams={}", queryParams);

    if (queryParams.isEmpty()) {
      // keine Suchkriterien
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
    if ("contact".contentEquals(key)) {
      return toSpecificationContactOptions(values);
    }

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
      case "lastName" -> lastName(value);
      case "email" -> email(value);
      case "gender" -> gender(value);
      case "maritalStatus" -> maritalStatus(value);
      case "zipCode" -> zipCode(value);
      case "city" -> city(value);
      default -> null;
    };
  }

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
    final var first = specs.removeFirst();
    return specs.stream().reduce(first, Specification::and);
  }

  private Specification<Customer> lastName(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.lastName)),
      builder.lower(builder.literal(STR."%\{teil}%"))
    );
  }

  private Specification<Customer> email(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.email)),
      builder.lower(builder.literal(STR."%\{teil}%"))
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


  private Specification<Customer> contactOptions(final String option) {
    final var contactOptionsType = ContactOptionsType.of(option);
    if (contactOptionsType == null) {
      return null;
    }
    return (root, query, builder) -> builder.like(
      root.get(Customer_.contactOptionsString),
      builder.literal(STR."%\{contactOptionsType.name()}%")
    );
  }

  private Specification<Customer> zipCode(final String prefix) {
    return (root, query, builder) -> builder.like(root.get(Customer_.address).get(Address_.zipCode), STR."%\{prefix}%");
  }

  private Specification<Customer> city(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.city)),
      builder.lower(builder.literal(STR."%\{prefix}%"))
    );
  }
}
