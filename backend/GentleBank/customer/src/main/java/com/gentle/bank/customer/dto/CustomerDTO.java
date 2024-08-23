package com.gentle.bank.customer.dto;

import com.gentle.bank.customer.entity.Customer;
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
 * DTO for {@link Customer}
 */
public record CustomerDTO(
  @NotNull(message = "du musst deinen Nachname eingeben!")
  @Pattern(regexp = LAST_NAME_PATTERN)
  @Size(max = NAME_MAX_LENGTH,message = "Der Nachname ist zu lang! sry:(")
  String lastName,

  @Size( max = NAME_MAX_LENGTH, message = "Der Vorname ist zu lang! sry:(")
  @Pattern(regexp = FIRST_NAME_PATTERN, message = "Dein Vorname (sollte) nur Buchstaben enthalten")
  @NotNull(message = "du musst deinen Vornamen eingeben!")
  String firstName,

  @Email(message = "keine gültige email!")
  @NotNull(message = "Die E-Mail-Adresse darf nicht null sein")
  @Size(max = EMAIL_MAX_LENGTH, message = "Die E-Mail-Adresse darf nicht länger als {max} Zeichen sein")
  String email,

  boolean isElite,

  @Past(message = "Das Geburtsdatum muss in der Vergangenheit liegen")
  LocalDate birthDate,

  @NotNull(message = "Das Geschlecht darf nicht null sein")
  GenderType gender,

  @NotNull(message = "Der Familienstand darf nicht null sein")
  MaritalStatusType maritalStatus,

  @UniqueElements
  @NotNull(message = "kontak optionen dürfen nicht null sein")
  List<ContactOptionsType> contactOptions,

  @NotNull(groups = OnCreate.class)
  AddressDTO address,

  @Pattern(regexp = USERNAME_PATTERN, message = "Der Benutzername muss zwischen 4 und 20 alphanumerischen Zeichen sein")
  @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "Der Benutzername muss zwischen 4 und 20 Zeichen lang sein")
  @NotNull(message = "Username darf nicht null sein")
  String username,
  String password
) {
  /**
   * Marker-Interface f&uuml;r Jakarta Validation: zus&auml;tzliche Validierung beim Neuanlegen.
   */
  public interface OnCreate { }

  /**
   * Muster für einen gültigen Nachnamen.
   */
  public static final String LAST_NAME_PATTERN = "(o'|von|von der|von und zu|van)?[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

  public static final String USERNAME_PATTERN = "[a-zA-Z0-9_\\-.]{4,}";
  public static final int USERNAME_MAX_LENGTH = 20;
  public static final int USERNAME_MIN_LENGTH = 4;

  private static final int EMAIL_MAX_LENGTH = 40;

  /**
   * Validierungsmuster und Konstanten für Feldlängen
   */

  public static final String FIRST_NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+(-[A-ZÄÖÜ][a-zäöüß]+)?";

  private static final int NAME_MAX_LENGTH = 40;
}
