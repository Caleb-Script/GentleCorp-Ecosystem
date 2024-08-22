package com.gentle.bank.customer.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class Constants {
    public static final String PROBLEM_PATH = "/problem";
    public static final String CUSTOMER_PATH = "/customer";

    /**
     * Muster für eine UUID.
     */
    public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";
    public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";
    public static final String LASTNAME_PATH = "/lastName";
}
