package com.gentle.bank.customer.util;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

/**
 * A utility class that contains constant values used throughout the application.
 * <p>
 * This class holds various constants including paths, patterns, and error messages
 * that are used for different purposes within the application. These constants are
 * utilized to ensure consistency and avoid magic numbers or strings throughout the codebase.
 * </p>
 * <p>
 * Constants in this class include:
 * <ul>
 *   <li>{@link #PROBLEM_PATH} - Base path for "type" within ProblemDetail.</li>
 *   <li>{@link #CUSTOMER_PATH} - Base path for customer-related operations.</li>
 *   <li>{@link #ID_PATTERN} - Regular expression pattern for UUIDs.</li>
 *   <li>{@link #VERSION_NUMBER_MISSING} - Message indicating that a version number is missing.</li>
 * </ul>
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public class Constants {

  /**
   * Base path for "type" within ProblemDetail.
   * <p>
   * This constant is used as a base path in the context of problem details in the application.
   * </p>
   */
  public static final String PROBLEM_PATH = "/problem";

  /**
   * Base path for customer-related operations.
   * <p>
   * This constant is used as a base path for operations related to customer management.
   * </p>
   */
  public static final String CUSTOMER_PATH = "/customer";

  /**
   * Regular expression pattern for UUIDs.
   * <p>
   * This constant defines the pattern used to validate UUIDs. The pattern ensures that
   * UUIDs are in the correct format.
   * </p>
   */
  public static final String ID_PATTERN = "[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}";

  /**
   * Message indicating that a version number is missing.
   * <p>
   * This constant is used as an error message when a version number is not provided
   * where it is required.
   * </p>
   */
  public static final String VERSION_NUMBER_MISSING = "Versionsnummer fehlt";
}
