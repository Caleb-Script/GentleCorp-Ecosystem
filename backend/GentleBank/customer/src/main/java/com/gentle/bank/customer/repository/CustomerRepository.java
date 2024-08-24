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

/**
 * Repository interface for managing {@link Customer} entities.
 * <p>
 * This repository interface extends {@link JpaRepository} and {@link JpaSpecificationExecutor} to provide
 * CRUD operations and query execution capabilities for {@link Customer} entities. It includes methods for
 * finding customers by various criteria and ensures that customer addresses are loaded eagerly using entity graphs.
 * </p>
 *
 * <p>Custom queries and methods include:</p>
 * <ul>
 *   <li>{@link #findAll()} - Retrieves all customers with their addresses.</li>
 *   <li>{@link #findAll(Specification)} - Retrieves customers based on the specified {@link Specification} with addresses.</li>
 *   <li>{@link #findById(UUID)} - Finds a customer by its ID, including the associated address.</li>
 *   <li>{@link #findByEmail(String)} - Finds customers by email address with case-insensitive matching.</li>
 *   <li>{@link #existsByEmail(String)} - Checks if a customer with the given email exists.</li>
 *   <li>{@link #findByLastName(CharSequence)} - Finds customers by last name with partial matching and sorting.</li>
 *   <li>{@link #findSurnamesByPrefix(String)} - Retrieves distinct last names starting with a given prefix.</li>
 * </ul>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

  /**
   * Retrieves all customers with their addresses eagerly fetched.
   *
   * @return a list of all customers with their addresses.
   */
  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  List<Customer> findAll();

  /**
   * Retrieves all customers matching the given specification with their addresses eagerly fetched.
   *
   * @param spec the specification to use for querying customers.
   * @return a list of customers matching the specification with their addresses.
   */
  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  List<Customer> findAll(@NonNull Specification<Customer> spec);

  /**
   * Finds a customer by its ID with the address eagerly fetched.
   *
   * @param id the ID of the customer to find.
   * @return an optional containing the customer if found, or empty if not found.
   */
  @EntityGraph(ADDRESS_GRAPH)
  @NonNull
  @Override
  Optional<Customer> findById(@NonNull UUID id);

  /**
   * Finds customers whose email address starts with the specified value (case-insensitive).
   *
   * @param email the email prefix to search for.
   * @return a collection of customers with emails starting with the specified prefix.
   */
  @Query("""
        SELECT C
        FROM   #{#entityName} C
        WHERE  lower(C.email) LIKE concat(lower(:email), '%')
        """)
  @EntityGraph(ADDRESS_GRAPH)
  Collection<Customer> findByEmail(String email);

  /**
   * Checks if a customer with the specified email address exists.
   *
   * @param email the email address to check.
   * @return {@code true} if a customer with the email exists, {@code false} otherwise.
   */
  boolean existsByEmail(String email);

  /**
   * Finds customers whose last name contains the specified value (case-insensitive) and orders the results by customer ID.
   *
   * @param lastName the last name prefix to search for.
   * @return a collection of customers whose last name contains the specified value.
   */
  @Query("""
        SELECT C
        FROM   #{#entityName} C
        WHERE  lower(C.lastName) LIKE concat('%', lower(:lastName), '%')
        ORDER BY C.id
        """)
  @EntityGraph(ADDRESS_GRAPH)
  Collection<Customer> findByLastName(CharSequence lastName);

  /**
   * Retrieves distinct last names of customers that start with the specified prefix (case-insensitive).
   *
   * @param prefix the prefix to search for.
   * @return a collection of distinct last names starting with the specified prefix.
   */
  @Query("""
        SELECT DISTINCT C.lastName
        FROM   #{#entityName} C
        WHERE  lower(C.lastName) LIKE concat(lower(:prefix), '%')
        ORDER BY C.lastName
        """)
  Collection<String> findSurnamesByPrefix(String prefix);
}
