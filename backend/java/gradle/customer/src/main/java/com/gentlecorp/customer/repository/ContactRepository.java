package com.gentlecorp.customer.repository;

import com.gentlecorp.customer.model.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {
}
