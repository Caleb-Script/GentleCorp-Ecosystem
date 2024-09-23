package com.gentlecorp.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("unchecked")
public class TestCreate extends CustomerCommonFunctions {

  @ParameterizedTest(name = "Erstelle einen Kunden mit dem {2}. Rang, der Email {1} und dem benutzernamen {0} ")
  @CsvSource({
    SUPREME_USERNAME + ", " + SUPREME_EMAIL + ", " + TIER_LEVEL_3,
    ELITE_USERNAME + ", " + ELITE_EMAIL + ", " + TIER_LEVEL_2,
    BASIC_USERNAME + ", " + BASIC_EMAIL + ", " + TIER_LEVEL_1
  })
  void testCreateCustomer(final String username, final String email, final int tierLevel) {
    // 1. Erstellen Sie den Kunden
    String newCustomerId = createCustomer(username, email, tierLevel);

    // 2. Überprüfen Sie den erstellten Kunden als Admin
    verifyCustomerAsAdmin(newCustomerId, username, email, tierLevel);

    // 3. Überprüfen Sie die Zugriffsrechte
    verifyAccessRights(newCustomerId);

    // 4. Erstellen Sie einen Basic-Kunden mit denselben Daten
    createAndVerifyCustomer(newCustomerId, username, email, tierLevel);

    // 5. Löschen Sie den Kunden und überprüfen Sie die Löschung
    deleteAndVerifyCustomer(newCustomerId);
  }


  @ParameterizedTest(name = "Überprüfen Sie die Erstellung eines Kunden mit ungültigen Daten: {0} = {1}")
  @CsvSource({
    EMAIL + ", " + INVALID_EMAIL + ", 'email': Please provide a valid email address., BAD_REQUEST",
    USERNAME + ", " + DUPLICATE_USERNAME + ", Der Benutzername gentlecg99 existiert bereits., UNPROCESSABLE_ENTITY",
    EMAIL + ", " + EXISTING_EMAIL + ", Die Emailadresse " + EXISTING_EMAIL + " existiert bereits, UNPROCESSABLE_ENTITY",
    LAST_NAME + ", " + INVALID_LAST_NAME + ", 'lastName': Invalid last name format., BAD_REQUEST",
    FIRST_NAME + ", " + INVALID_FIRST_NAME + ", 'firstName': First name should only contain letters., BAD_REQUEST",
    PHONE_NUMBER + ", " + INVALID_PHONE_NUMBER + ", Please provide a valid phone number., BAD_REQUEST",
    USERNAME + ", " + INVALID_USERNAME + ", Username can only contain alphanumeric characters; underscores; dots; or hyphens# Username must be between 4 and 20 characters long, BAD_REQUEST",
    TIER_LEVEL + ", " + INVALID_TIER_LEVEL + ", Tier level must be at most 3, BAD_REQUEST",
    BIRTHDATE + ", " + FUTURE_BIRTHDATE + ", Birthdate must be a past date, BAD_REQUEST",
    GENDER + ", " + INVALID_GENDER + ", 'gender': Please specify your gender., BAD_REQUEST",
    MARITAL_STATUS + ", " + INVALID_MARITAL_STATUS + ", 'maritalStatus': Please specify your marital status., BAD_REQUEST",
    INTERESTS + ", " + DUPLICATE_INTERESTS + ", Interests must be unique, BAD_REQUEST",
    CONTACT_OPTIONS + ", " + DUPLICATE_CONTACT_OPTIONS + ", Contact options must be unique, BAD_REQUEST"
  })
  void testCreateCustomerWithInvalidData(final String attributName, String invalidData, String expectedError, HttpStatus expectedStatus) {
    HttpEntity<Map<String, Object>> request = createRequestBody(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);

    if (FUTURE_BIRTHDATE.equals(invalidData)) {
      invalidData = LocalDate.now().plusDays(1).toString();
    }

    Map<String, Object> customerData = (Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER);
    if (INTERESTS.equals(attributName) || CONTACT_OPTIONS.equals(attributName)) {
      customerData.put(attributName, List.of(invalidData, invalidData));
    } else {
      customerData.put(attributName, invalidData);
    }

    var response = testClientProvider.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
    assertThat(response.getBody()).isNotNull();
    if(invalidData.equals(INVALID_USERNAME)) {
      final var kp = expectedError.replace(";", ",");
      final var errorDetails = kp.split("#");
      assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains(errorDetails);
    } else {
      assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains(expectedError);
    }
  }

  @Test
  void testCreateCustomerWithoutAddress() {
    var request = createRequestBody(DUPLICATE_USERNAME, ELITE_EMAIL, TIER_LEVEL_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).remove(ADDRESS);

    var response = testClientProvider.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Der Benutzername gentlecg99 existiert bereits.");
  }
}
