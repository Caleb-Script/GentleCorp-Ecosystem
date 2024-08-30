package com.gentle.bank.customer.service.patch;

import com.gentle.bank.customer.mapper.CustomerInputMapper;
import com.gentle.bank.customer.entity.Customer;
import com.gentle.bank.customer.entity.enums.*;
import com.gentle.bank.customer.service.CustomerWriteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import static com.gentle.bank.customer.service.patch.PatchOperationType.*;

@Component
@Slf4j
@RequiredArgsConstructor
public final class CustomerPatcher {
    private final CustomerInputMapper customerInputMapper;
    private final CustomerWriteService customerWriteService;

    /**
     * PATCH-Operationen werden auf ein Customer-Objekt angewandt.
     *
     * @param customer      Das zu modifizierende Customer-Objekt.
     * @param operations Die anzuwendenden Operationen.
     * @param request    Das Request-Objekt, um ggf. die URL für ProblemDetail zu ermitteln
     * @throws InvalidPatchOperationException Falls die Patch-Operation nicht korrekt ist.
     */
    public void patch(
            final Customer customer,
            final Collection<PatchOperation> operations,
            final HttpServletRequest request
    ) {
        final var replaceOps = operations.stream()
                .filter(op -> op.operationType() == REPLACE)
                .toList();
        log.debug("patch: replaceOps={}", replaceOps);
        final var uri = URI.create(request.getRequestURL().toString());
        replaceOps(customer, replaceOps, uri);

        final var addOps = operations.stream()
                .filter(op -> op.operationType() == ADD)
                .toList();
        log.debug("patch: addOps={}", addOps);
        addOps(customer, addOps, uri);

        final var removeOps = operations.stream()
                .filter(op -> op.operationType() == REMOVE)
                .toList();
        log.debug("patch: removeOps={}", removeOps);
        removeOps(customer, removeOps, uri);
    }

    private void replaceOps(final Customer customer, @NonNull final Iterable<@NonNull PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "surname" -> customer.setLastName(op.value());
                case "email" -> customer.setEmail(op.value());
                case "gender" -> customer.setGender(GenderType.of(op.value()));
                case "maritalStatus" -> customer.setMaritalStatus(MaritalStatusType.of(op.value()));
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("replaceOps: customer={}", customer);
    }

    private void addOps(final Customer customer, final Collection<PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "contactOption" -> addContactOption(customer, op, uri);
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("addOps: customer={}", customer);
    }

    private void removeOps(final Customer customer, @NonNull final Collection<@NonNull PatchOperation> ops, final URI uri) {
        ops.forEach(op -> {
            switch (op.path()) {
                case "contactOption" -> removeContactOption(customer, op, uri);
                default -> throw new InvalidPatchOperationException(uri);
            }
        });
        log.trace("removeOps: customer={}", customer);
    }

    private void addContactOption(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("adding contactOption={}",op.value());

        final var contactOption = ContactOptionsType.of(op.value());
        if (contactOption == null)
            throw new InvalidPatchOperationException(uri);

        final var contactOptions = customer.getContactOptions() == null
                ? new ArrayList<ContactOptionsType>(ContactOptionsType.values().length)
                : new ArrayList<>(customer.getContactOptions());
        if (contactOptions.contains(contactOption))
            throw new InvalidPatchOperationException(uri);

        contactOptions.add(contactOption);

        log.trace("addContactOption: contactOptions={}", op, contactOptions);
        customer.setContactOptions(contactOptions);
    }

    private void removeContactOption(final Customer customer, final PatchOperation op, final URI uri) {
        log.debug("removing contactOption={}",op.value());

        final var contactOption = ContactOptionsType.of(op.value());
        if (contactOption == null) {
            throw new InvalidPatchOperationException(uri);
        }
        final var contactOptions = customer.getContactOptions()
                .stream()
                .filter(contactOptionTmp -> contactOptionTmp != contactOption)
                .toList();
        customer.setContactOptions(contactOptions);
        log.debug("removed contactOption={}", contactOption);
    }
}
