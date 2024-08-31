package com.gentlecorp.transaction.repository;

import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.entity.Transaction;
import com.gentlecorp.transaction.model.entity.Transaction_;
import com.gentlecorp.transaction.model.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
  public Optional<Specification<Transaction>> build(final Map<String, ? extends List<String>> queryParams) {
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

  private Specification<Transaction> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
//      case "type" -> transactionType(value);
      case "sender" -> sender(value);
      case "receiver" -> receiver(value);
      default -> throw new IllegalArgumentException(key);
    };
  }


//  private Specification<Transaction> transactionType(final String value) {
//    return (root, query, builder) -> builder.equal(
//      root.get(Transaction_.type),
//      TransactionType.of(value)
//    );
//  }

  private Specification<Transaction> sender(final String value) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Transaction_.sender.toString())),
      builder.lower(builder.literal("%" + value + "%"))
    );
  }

  private Specification<Transaction> receiver(final String value) {
    return (root, query, builder) -> builder.like(
      builder.lower(root.get(Transaction_.receiver.toString())),
      builder.lower(builder.literal("%" + value + "%"))
    );
  }
}
