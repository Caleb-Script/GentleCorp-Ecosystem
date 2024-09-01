package com.gentlecorp.invoice.repository;

import com.gentlecorp.invoice.model.entity.Invoice;
import com.gentlecorp.invoice.model.entity.Invoice_;
import com.gentlecorp.invoice.model.enums.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
  public Optional<Specification<Invoice>> build(final Map<String, ? extends List<String>> queryParams) {
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

  private Specification<Invoice> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
      case "type" -> invoiceType(value);
//      case "sender" -> sender(value);
//      case "receiver" -> receiver(value);
      default -> throw new IllegalArgumentException(key);
    };
  }


  private Specification<Invoice> invoiceType(final String value) {
    return (root, query, builder) -> builder.equal(
      root.get(Invoice_.type),
      StatusType.of(value)
    );
  }

//  private Specification<Invoice> sender(final String value) {
//    return (root, query, builder) -> builder.like(
//      builder.lower(root.get(Invoice_.sender.toString())),
//      builder.lower(builder.literal("%" + value + "%"))
//    );
//  }
//
//  private Specification<Invoice> receiver(final String value) {
//    return (root, query, builder) -> builder.like(
//      builder.lower(root.get(Invoice_.receiver.toString())),
//      builder.lower(builder.literal("%" + value + "%"))
//    );
//  }
}
