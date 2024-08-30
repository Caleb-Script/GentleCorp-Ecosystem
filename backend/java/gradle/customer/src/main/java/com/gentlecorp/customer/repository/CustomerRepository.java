package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import static com.gentlecorp.customer.model.entity.Customer.ADDRESS_GRAPH;
import static com.gentlecorp.customer.model.entity.Customer.ALL_GRAPH;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  List<Customer> findAll();

  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  List<Customer> findAll (Specification<Customer> spec);

  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  Optional<Customer> findById(@NonNull UUID id);

  @Query("""
        SELECT DISTINCT c
        FROM     #{#entityName} c
        WHERE    c.id = :id
        """)
  @EntityGraph(ALL_GRAPH)
  @NonNull
  Optional<Customer> findByIdFetchAll(UUID id);

  boolean existsByEmail(String email);
}
