package com.gentlecorp.invoice.repository;

import com.gentlecorp.invoice.model.entity.Invoice;
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
public interface InvoiceRepository extends JpaRepository<Invoice, UUID>, JpaSpecificationExecutor<Invoice> {
  @NonNull
  @Override
  List<Invoice> findAll();

  @NonNull
  @Override
  List<Invoice> findAll (Specification<Invoice> spec);

  @NonNull
  @Override
  Optional<Invoice> findById(@NonNull UUID id);

  @Query("""
        SELECT DISTINCT i
        FROM     #{#entityName} i
        WHERE    i.accountId = :id
        """)
  @NonNull
  List<Invoice> findByAccountId(UUID id);
}
