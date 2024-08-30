package com.gentlecorp.account.repository;

import com.gentlecorp.account.model.entity.Account;
import com.gentlecorp.account.model.entity.Account_;
import com.gentlecorp.account.model.enums.AccountType;
import com.gentlecorp.account.model.enums.StatusType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificationBuilder {
  public Optional<Specification<Account>> build(final Map<String, ? extends List<String>> queryParams) {
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

  private Specification<Account> toSpecification(final Map.Entry<String, ? extends List<String>> entry) {
    log.trace("toSpec: entry={}", entry);
    final var key = entry.getKey();
    final var values = entry.getValue();

    if (values == null || values.size() != 1) {
      return null;
    }

    final var value = values.getFirst();
    return switch (key) {
      case "category" -> category(value);
      case "status" -> state(value);
      default -> throw new IllegalArgumentException(key);
    };
  }


  private Specification<Account> category(final String value) {
    return (root, query, builder) -> builder.equal(
      root.get(Account_.category),
      AccountType.of(value)
    );
  }

  private Specification<Account> state(final String value) {
    return (root, query, builder) -> builder.equal(
      root.get(Account_.state),
      StatusType.of(value)
    );
  }
}
