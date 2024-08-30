package com.gentlecorp.customer.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum InterestType {
    INVESTMENTS("Investments"),
    SAVING_AND_FINANCE("Savings and Finances"),
    CREDIT_AND_DEBT("Credit and Debts"),
    BANK_PRODUCTS_AND_SERVICES("Bank Products and Services"),
    FINANCIAL_EDUCATION_AND_COUNSELING("Financial Education and Counseling"),
    REAL_ESTATE("Real Estate"),
    INSURANCE("Insurance"),
    SUSTAINABLE_FINANCE("Sustainable Finance"),
    TECHNOLOGY_AND_INNOVATION("Technology and Innovation"),
    TRAVEL("Travel");

    private final String interest;


    @JsonCreator
    public static InterestType of(final String value) {
        return Stream.of(values())
                .filter(interestType -> interestType.interest.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
