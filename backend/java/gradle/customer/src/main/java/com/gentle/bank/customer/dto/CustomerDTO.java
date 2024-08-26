package com.gentle.bank.customer.dto;

import com.gentle.bank.customer.entity.enums.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object (DTO) for representing a customer.
 * <p>
 * This DTO encapsulates the details of a customer, including personal information such as name, email, birthdate,
 * gender, marital status, contact options, address, and username. It is used for transferring customer data between
 * different layers of the application, particularly in requests and responses involving customer information.
 * </p>
 *
 * @param lastName The last name of the customer. Must not be {@code null} and must match the {@link #LAST_NAME_PATTERN} regex.
 * @param firstName The first name of the customer. Must not be {@code null} and must match the {@link #FIRST_NAME_PATTERN} regex.
 * @param email The email address of the customer. Must not be {@code null} and must be a valid email format.
 * @param isElite A flag indicating whether the customer is considered elite.
 * @param birthDate The birthdate of the customer. Must be in the past.
 * @param gender The gender of the customer. Must not be {@code null}.
 * @param maritalStatus The marital status of the customer. Must not be {@code null}.
 * @param contactOptions A list of contact options for the customer. Must not be {@code null} and all elements must be unique.
 * @param address The address of the customer. Must not be {@code null} and must be validated for creation with the {@link OnCreate} group.
 * @param username The username of the customer. Must not be {@code null} and must match the {@link #USERNAME_PATTERN} regex.
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
public record CustomerDTO(
  /**
   * The last name of the customer. This field must not be {@code null} and must match the {@link #LAST_NAME_PATTERN} regex.
   * <p>
   * The pattern allows for last names with optional prefixes like "von", "van", and hyphens.
   * </p>
   */
  @NotNull(message = "You must enter your last name!")
  @Pattern(regexp = LAST_NAME_PATTERN)
  @Size(max = NAME_MAX_LENGTH, message = "The last name is too long. Sorry :(")
  String lastName,

  /**
   * The first name of the customer. This field must not be {@code null} and must match the {@link #FIRST_NAME_PATTERN} regex.
   * <p>
   * The pattern requires the first name to start with a capital letter and allows for hyphens.
   * </p>
   */
  @Size(max = NAME_MAX_LENGTH, message = "The first name is too long. Sorry :(")
  @Pattern(regexp = FIRST_NAME_PATTERN, message = "Your first name should only contain letters")
  @NotNull(message = "You must enter your first name!")
  String firstName,

  /**
   * The email address of the customer. This field must not be {@code null} and must be a valid email format.
   */
  @Email(message = "Invalid email address!")
  @NotNull(message = "The email address must not be null")
  @Size(max = EMAIL_MAX_LENGTH, message = "The email address must not be longer than {max} characters")
  String email,

  /**
   * A flag indicating whether the customer is considered elite.
   */
  boolean isElite,

  /**
   * The birthdate of the customer. This field must be in the past.
   */
  @Past(message = "The birthdate must be in the past")
  LocalDate birthDate,

  /**
   * The gender of the customer. This field must not be {@code null}.
   */
  @NotNull(message = "The gender must not be null")
  GenderType gender,

  /**
   * The marital status of the customer. This field must not be {@code null}.
   */
  @NotNull(message = "The marital status must not be null")
  MaritalStatusType maritalStatus,

  /**
   * A list of contact options for the customer. This field must not be {@code null} and all elements must be unique.
   */
  @UniqueElements
  @NotNull(message = "Contact options must not be null")
  List<ContactOptionsType> contactOptions,

  /**
   * The address of the customer. This field must not be {@code null} and must be validated for creation with the {@link OnCreate} group.
   */
  @NotNull(groups = OnCreate.class)
  AddressDTO address,

  /**
   * The username of the customer. This field must not be {@code null} and must match the {@link #USERNAME_PATTERN} regex.
   * <p>
   * The pattern requires the username to be between 4 and 20 alphanumeric characters.
   * </p>
   */
  @Pattern(regexp = USERNAME_PATTERN, message = "The username must be between 4 and 20 alphanumeric characters")
  @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "The username must be between 4 and 20 characters long")
  @NotNull(message = "Username must not be null")
  String username
) {
  /**
   * Marker interface for Jakarta Validation: additional validation during creation.
   */
  public interface OnCreate { }

  /**
   * Pattern for a valid last name. Allows optional prefixes and hyphens.
   */
  public static final String LAST_NAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

  /**
   * Pattern for a valid username. Must be between 4 and 20 alphanumeric characters.
   */
  public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";

  public static final int USERNAME_MAX_LENGTH = 20;
  public static final int USERNAME_MIN_LENGTH = 4;

  private static final int EMAIL_MAX_LENGTH = 40;

  /**
   * Validation pattern for the first name. Must start with a capital letter and may include hyphens.
   */
  public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

  private static final int NAME_MAX_LENGTH = 40;
}
