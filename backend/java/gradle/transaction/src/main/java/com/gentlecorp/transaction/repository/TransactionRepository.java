package com.gentlecorp.transaction.repository;

import com.gentlecorp.transaction.model.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
  @NonNull
  @Override
  List<Transaction> findAll();

  @NonNull
  @Override
  List<Transaction> findAll (Specification<Transaction> spec);

  @NonNull
  @Override
  Optional<Transaction> findById(@NonNull UUID id);

  @Query("""
        SELECT DISTINCT t
        FROM     #{#entityName} t
        WHERE    t.sender = :id or t.receiver = :id
        """)
  @NonNull
  List<Transaction> findByAccountId(UUID id);
}
