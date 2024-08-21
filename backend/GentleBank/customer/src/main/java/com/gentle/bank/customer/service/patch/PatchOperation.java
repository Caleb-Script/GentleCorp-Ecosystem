package com.gentle.bank.customer.service.patch;

public record PatchOperation(
    PatchOperationType operationType,
    String path,
    String value
) {
}
