package com.gentlecorp.customer.model.dto;

import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.List;

public record CustomerDTO(
  @NotNull(message = "You must enter your last name!")
  @Pattern(regexp = LAST_NAME_PATTERN)
  @Size(max = NAME_MAX_LENGTH, message = "The last name is too long. Sorry :(")
  String lastName,

  @Size(max = NAME_MAX_LENGTH, message = "The first name is too long. Sorry :(")
  @Pattern(regexp = FIRST_NAME_PATTERN, message = "Your first name should only contain letters")
  @NotNull(message = "You must enter your first name!")
  String firstName,

  @Email(message = "Invalid email address!")
  @NotNull(message = "The email address must not be null")
  @Size(max = EMAIL_MAX_LENGTH, message = "The email address must not be longer than {max} characters")
  String email,

  String phoneNumber,

  @Pattern(regexp = USERNAME_PATTERN, message = "The username must be between 4 and 20 alphanumeric characters")
  @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "The username must be between 4 and 20 characters long")
  @NotNull(message = "Username must not be null")
  String username,

  @Min(MIN_LEVEL)
  @Max(MAX_LEVEL)
  int tierLevel,

  boolean isSubscribed,

  @Past(message = "The birthdate must be in the past")
  LocalDate birthDate,

  @NotNull(message = "The gender must not be null")
  GenderType gender,

  @NotNull(message = "The marital status must not be null")
  MaritalStatusType maritalStatus,

  @UniqueElements(message = "unterschied!")
  List<InterestType> interests,

  @UniqueElements
  @NotNull(message = "Contact options must not be null")
  List<ContactOptionsType> contactOptions,

  @NotNull(groups = OnCreate.class)
  AddressDTO address
) {
  public interface OnCreate { }

  public static final long MIN_LEVEL= 1L;
  public static final long MAX_LEVEL = 3L;
  public static final String LAST_NAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
  public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";
  public static final int USERNAME_MAX_LENGTH = 20;
  public static final int USERNAME_MIN_LENGTH = 4;
  private static final int EMAIL_MAX_LENGTH = 40;
  public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";
  public static final int NAME_MAX_LENGTH = 40;
}
