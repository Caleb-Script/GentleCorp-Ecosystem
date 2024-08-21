package com.gentle.bank.customer.repository;

import com.gentle.bank.customer.entity.Customer;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gentle.bank.customer.entity.Customer.ADDRESS_GRAPH;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    List<Customer> findAll();

    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    List<Customer> findAll(@NonNull Specification<Customer> spec);

    @EntityGraph(ADDRESS_GRAPH)
    @NonNull
    @Override
    Optional<Customer> findById(@NonNull UUID id);

    @Query("""
        SELECT C
        FROM   Customer C
        WHERE  lower(C.email) LIKE concat(lower(:email), '%')
        """)
    @EntityGraph(ADDRESS_GRAPH)
    Collection<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
        SELECT   C
        FROM     Customer C
        WHERE    lower(C.lastName) LIKE concat('%', lower(:lastName), '%')
        ORDER BY C.id
        """)
    @EntityGraph(ADDRESS_GRAPH)
    Collection<Customer> findByLastName(CharSequence lastName);

    @Query("""
        SELECT DISTINCT C.lastName
        FROM     Customer C
        WHERE    lower(C.lastName) LIKE concat(lower(:prefix), '%')
        ORDER BY C.lastName
        """)
    Collection<String> findSurnamesByPrefix(String prefix);
}
