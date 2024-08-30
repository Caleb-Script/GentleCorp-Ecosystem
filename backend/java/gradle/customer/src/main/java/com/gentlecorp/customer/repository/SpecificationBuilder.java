package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Address_;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.entity.Customer_;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
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
      case "elite" -> tierLevel(value);
      case "gender" -> gender(value);
      case "maritalStatus" -> maritalStatus(value);
      case "zipCode" -> zipCode(value);
      case "city" -> city(value);
      case "state" -> state(value);
      case "country" -> country(value);
      default -> throw new IllegalArgumentException(key);
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
    final var first = specs.remove(0);
    return specs.stream().reduce(first, Specification::and);
  }

  private Specification<Customer> lastName(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.lastName)),
      builder.lower(builder.literal("%" + teil + "%"))
    );
  }

  private Specification<Customer> email(final String teil) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.email)),
      builder.lower(builder.literal("%" + teil + "%"))
    );
  }

  @SuppressWarnings({"CatchParameterName", "LocalFinalVariableName"})
  private Specification<Customer> tierLevel(final String tierLevel) {
    final int tierLevelInt;
    try {
      tierLevelInt = Integer.parseInt(tierLevel);
    } catch (final NumberFormatException _) {
      //noinspection ReturnOfNull
      return null;
    }
    return (root, _, builder) -> builder.equal(root.get(Customer_.tierLevel), tierLevelInt);
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
      builder.literal("%" + contactOptionsType.name() + "%")
    );
  }

  private Specification<Customer> zipCode(final String prefix) {
    return (root, query, builder) -> builder.like(
      root.get(Customer_.address)
        .get(Address_.zipCode), "%" + prefix + "%"
    );
  }

  private Specification<Customer> city(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.city)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }

  private Specification<Customer> state(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.state)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }

  private Specification<Customer> country(final String prefix) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Customer_.address).get(Address_.country)),
      builder.lower(builder.literal("%" + prefix + "%"))
    );
  }
}
