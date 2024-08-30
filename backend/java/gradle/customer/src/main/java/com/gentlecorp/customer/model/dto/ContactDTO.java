package com.gentlecorp.customer.model.dto;

import com.gentlecorp.customer.model.enums.RelationshipType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.gentlecorp.customer.model.dto.CustomerDTO.FIRST_NAME_PATTERN;
import static com.gentlecorp.customer.model.dto.CustomerDTO.LAST_NAME_PATTERN;
import static com.gentlecorp.customer.model.dto.CustomerDTO.NAME_MAX_LENGTH;

public record ContactDTO(
  @NotNull(message = "You must enter your last name!")
  @Pattern(regexp = LAST_NAME_PATTERN)
  @Size(max = NAME_MAX_LENGTH, message = "The last name is too long. Sorry :(")
  String lastName,

  @NotNull(message = "You must enter your last name!")
  @Pattern(regexp = FIRST_NAME_PATTERN)
  @Size(max = NAME_MAX_LENGTH, message = "The last name is too long. Sorry :(")
  String firstName,

  @NotNull
  RelationshipType relationship,
  int withdrawalLimit,
  boolean isEmergencyContact,
  LocalDate startDate,
  LocalDate endDate
) {
}
