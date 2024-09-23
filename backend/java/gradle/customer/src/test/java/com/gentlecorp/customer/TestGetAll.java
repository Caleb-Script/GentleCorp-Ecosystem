package com.gentlecorp.customer;

import com.gentlecorp.customer.model.CustomerResponse;
import com.gentlecorp.customer.model.TestCustomer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


public class TestGetAll extends CustomerCommonFunctions {

  private static final Logger log = LoggerFactory.getLogger(TestGetAll.class);

  private String buildUrl(String paramName, String paramValue) {
    final var builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
    if (paramName != null && paramValue != null) {
      builder.queryParam(paramName, paramValue);
    }
    return builder.toUriString();
  }

  private void assertResponseStatus(final TestRestTemplate client, String url, final HttpStatus expectedStatus) {
    final var response = client.getForEntity(url, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
  }

  private void assertForbiddenResponse(final TestRestTemplate client, final String url) {
    assertResponseStatus(client, url, HttpStatus.FORBIDDEN);
  }

  private void assertUnauthorizedResponse(final TestRestTemplate client, final String url) {
    assertResponseStatus(client, url, HttpStatus.UNAUTHORIZED);
  }

  private void assertBadRequestResponse(final String url, final String Key) {
    final var response = testClientProvider.adminClient.getForEntity(url, ProblemDetail.class);
    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
      () -> assertThat(response.getBody()).isNotNull(),
      () -> assertThat(response.getBody().getType().toString()).isEqualTo(BAD_REQUEST_TYPE),
      () -> assertThat(response.getBody().getTitle()).isEqualTo(BAD_REQUEST_TITLE),
      () -> assertThat(response.getBody().getStatus()).isEqualTo(BAD_REQUEST_STATUS),
      () -> assertThat(response.getBody().getDetail()).isEqualTo(INVALID_KEY + Key),
      () -> assertThat(response.getBody().getInstance().toString()).startsWith(SCHEMA_HOST + port + CUSTOMER_PATH)
    );
  }

  private CustomerResponse getCustomerResponse(final TestRestTemplate client, final String url) {
    final var response = client.getForEntity(url, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    return response.getBody();
  }

  private void assertValidCustomerResponse(CustomerResponse customerResponse) {
    assertAll(
      () -> assertThat(customerResponse._embedded()).isNotNull(),
      () -> assertThat(customerResponse._embedded().customers())
        .isNotNull()
        .isNotEmpty()
        .hasSize(TOTAL_CUSTOMERS),
      () -> assertThat(customerResponse._embedded().customers())
        .extracting(TestCustomer::username)
        .contains("admin", "rae", "erik")
    );
  }

  @Nested
  @DisplayName("Tests für verschiedene Benutzerrollen")
  class UserRoleTests {

    private final String url = buildUrl(null, null);

    @Test
    @DisplayName("Sollte alle Kunden für Admin zurückgeben")
    void testGetAllAsAdmin() {
      final var customerResponse = getCustomerResponse(testClientProvider.adminClient, url);
      assertValidCustomerResponse(customerResponse);
    }

    @Test
    @DisplayName("Sollte alle Kunden für User zurückgeben")
    void testGetAllAsUser() {
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.userClient, url);
      assertValidCustomerResponse(customerResponse);
    }

    @Test
    @DisplayName("Sollte Zugriff für Supreme verweigern")
    void testGetAllAsSupreme() {
      assertForbiddenResponse(testClientProvider.supremeClient, url);
    }

    @Test
    @DisplayName("Sollte Zugriff für Elite verweigern")
    void testGetAllAsElite() {
      assertForbiddenResponse(testClientProvider.eliteClient, url);
    }

    @Test
    @DisplayName("Sollte Zugriff für Basic verweigern")
    void testGetAllAsBasic() {
      assertForbiddenResponse(testClientProvider.basicClient, url);
    }

    @Test
    @DisplayName("Sollte Zugriff für Besucher verweigern")
    void testGetAllAsVisitor() {
      assertUnauthorizedResponse(testClientProvider.visitorClient, url);
    }
  }

  @DisplayName("Filter Kunden nach Geschlecht")
  @ParameterizedTest(name = "Sollte {1} Kunden für Geschlecht {0} zurückgeben")
  @CsvSource({
    GENDER_MALE + ", 13",
    GENDER_FEMALE + ", 11",
    GENDER_DIVERSE + ", 3"
  })
  void testGetAllFilterGender(String gender, int expectedSize) {
    String url = buildUrl(GENDER, gender);

    var customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

    assertAll(
      () -> assertThat(customerResponse._embedded()).isNotNull(),
      () -> assertThat(customerResponse._embedded().customers())
        .isNotNull()
        .isNotEmpty()
        .hasSize(expectedSize),
      () -> assertThat(customerResponse._embedded().customers())
        .allMatch(customer -> customer.gender().getGender().equals(gender))
    );
  }

  @Nested
  @DisplayName("Tests für Benutzernamen-Filter")
  class UsernameFilterTests {

    @ParameterizedTest(name = "Sollte einen Kunden für exakten Benutzernamen {0} zurückgeben")
    @CsvSource({
      USERNAME_LEROY + ", " + USERNAME_LEROY + ", 1",
      USERNAME_CALEB + ", " + USERNAME_CALEB + ", 1",
      USERNAME_ERIK + "," + USERNAME_ERIK + ", 1",
    })
    void testGetAllFilterUsername(String searchUsername, String expectedUsername, int expectedSize) {
      String url = buildUrl(USERNAME, searchUsername);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers().getFirst().username())
          .isEqualTo(expectedUsername)
      );
    }

    @ParameterizedTest(name = "Sollte Kunden für Teilbenutzernamen {0} zurückgeben")
    @CsvSource({
      QUERY_SON + ", 3",
      QUERY_IVA + ", 2"
    })
    void testGetAllFilterPartialUsername(String partialUsername, int expectedSize) {
      String url = buildUrl(USERNAME, partialUsername);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.username().toLowerCase().contains(partialUsername.toLowerCase())),
        () -> assertThat(customerResponse._embedded().customers().getFirst().username().toLowerCase())
          .contains(partialUsername.toLowerCase())
      );
    }
  }

  @Nested
  @DisplayName("Tests für Präfix-Filter")
  class PrefixFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Präfix {0} zurückgeben")
    @CsvSource({
      QUERY_IVA + ", 2",
      QUERY_G + ", 4",
      QUERY_M + ", 2"
    })
    void testGetAllFilterPrefix(String prefix, int expectedSize) {
      String url = buildUrl(PREFIX, prefix);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.lastName().toLowerCase().startsWith(prefix.toLowerCase())),
        () -> assertThat(customerResponse._embedded().customers().getFirst().lastName().toLowerCase())
          .startsWith(prefix.toLowerCase())
      );

      // Optional: Überprüfen Sie auch den letzten Benutzer
      if (expectedSize > 1) {
        assertThat(customerResponse._embedded().customers().get(expectedSize - 1).lastName().toLowerCase())
          .startsWith(prefix.toLowerCase());
      }
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierenden Präfix zurückgeben")
    void testGetAllFilterNonExistingPrefix() {
      String url = buildUrl(PREFIX, QUERY_XYZ);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull());
    }
  }

  @Nested
  @DisplayName("Tests für Nachnamen-Filter")
  class LastNameFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Nachnamen-Filter {0} zurückgeben")
    @CsvSource({
      QUERY_M + ", 11",
      QUERY_SON + ", 4"
    })
    void testGetAllFilterLastName(String lastNameFilter, int expectedSize) {
      String url = buildUrl(LAST_NAME, lastNameFilter);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.lastName().toLowerCase().contains(lastNameFilter.toLowerCase()))
      );
    }

    @Test
    @DisplayName("Sollte spezifische Nachnamen für Filter 'M' enthalten")
    void testGetAllFilterLastName_M_SpecificNames() {
      String url = buildUrl(LAST_NAME, QUERY_M);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      List<String> expectedLastNames = Arrays.asList("Meyer", "Müller", "Mustermann");
      assertThat(customerResponse._embedded().customers())
        .extracting(TestCustomer::lastName)
        .anyMatch(expectedLastNames::contains);
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierenden Nachnamen zurückgeben")
    void testGetAllFilterNonExistingLastName() {
      String url = buildUrl(LAST_NAME, QUERY_XYZ);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }
  }

  @Nested
  @DisplayName("Tests für E-Mail-Filter")
  class EmailFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für E-Mail-Filter {0} zurückgeben")
    @CsvSource({
      EMAIL_CALEB + ", 1",
      QUERY_IVANOV + ", 2",
      QUERY_ICLOUD_COM + ", 2"
    })
    void testGetAllFilterEmail(String emailFilter, int expectedSize) {
      String url = buildUrl(EMAIL, emailFilter);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.email().toLowerCase().contains(emailFilter.toLowerCase()))
      );
    }

    @Test
    @DisplayName("Sollte exakte E-Mail-Adresse zurückgeben")
    void testGetAllFilterExactEmail() {
      String url = buildUrl(EMAIL, EMAIL_CALEB);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(1),
        () -> assertThat(customerResponse._embedded().customers().getFirst().email().toLowerCase())
          .isEqualTo(EMAIL_CALEB.toLowerCase())
      );
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierende E-Mail zurückgeben")
    void testGetAllFilterNonExistingEmail() {
      String url = buildUrl(EMAIL, "nonexistent@example.com");
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }
  }

  @Nested
  @DisplayName("Tests für Abonnement-Filter")
  class SubscriptionFilterTests {

    @ParameterizedTest(name = "Sollte Kunden basierend auf Abonnement-Status ({0}) zurückgeben")
    @CsvSource({
      QUERY_IS_SUBSCRIBED + ", 23, true",
      QUERY_IS_NOT_SUBSCRIBED + ", 4, false"
    })
    void testGetAllFilterSubscription(String subscriptionStatus, int expectedSize, boolean expectedSubscriptionStatus) {
      String url = buildUrl(IS_SUBSCRIBED, subscriptionStatus);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.subscribed() == expectedSubscriptionStatus)
      );
    }

    //TODO verbessern
//    @Test
//    @DisplayName("Sollte leere Liste für ungültigen Abonnement-Status zurückgeben aber es kommt false")
//    void testGetAllFilterInvalidSubscriptionStatus() {
//      String url = buildUrl(IS_SUBSCRIBED, INVALID);
//      assertBadRequestResponse(url, INVALID);
//    }
  }

  @Nested
  @DisplayName("Tests für Tier-Level-Filter")
  class TierLevelFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Tier-Level {0} zurückgeben")
    @CsvSource({
      TIER_LEVEL_1 + ", 9",
      TIER_LEVEL_2 + ", 9",
      TIER_LEVEL_3 + ", 9"
    })
    void testGetAllFilterTierLevel(String tierLevel, int expectedSize) {
      String url = buildUrl(TIER_LEVEL, tierLevel);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.tierLevel() == Integer.parseInt(tierLevel))
      );
    }

    @Test
    @DisplayName("Sollte spezifische Tier 1 Kunden enthalten")
    void testGetAllFilterTier1SpecificCustomers() {
      String url = buildUrl(TIER_LEVEL, String.valueOf(TIER_LEVEL_1));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      List<String> expectedTier1Customers = Arrays.asList("julia", "erik", "john.muller");
      assertThat(customerResponse._embedded().customers())
        .extracting(TestCustomer::username)
        .containsAnyElementsOf(expectedTier1Customers);
    }

    @Test
    @DisplayName("Sollte leere Liste für ungültigen Tier-Level zurückgeben")
    void testGetAllFilterInvalidTierLevel() {
      String url = buildUrl(TIER_LEVEL, String.valueOf(INVALID_TIER_LEVEL_4));
      final var customerResponse = getCustomerResponse(testClientProvider.adminClient, url);
      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }
  }

  @Nested
  @DisplayName("Tests für Geburtsdatum-Filter")
  class BirthdateFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Geburtsdatum-Filter {0} zurückgeben")
    @CsvSource({
      QUERY_BIRTH_DATE_BEFORE + ", 18, before",
      QUERY_BIRTH_DATE_AFTER + ", 4, after",
      QUERY_BIRTH_DATE_BETWEEN + ", 5, between"
    })
    void testGetAllFilterBirthdate(String birthdateFilter, int expectedSize, String filterType) {
      String url = buildUrl(BIRTHDATE, birthdateFilter);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> matchesBirthdateFilter(customer, birthdateFilter, filterType))
      );
    }

    private boolean matchesBirthdateFilter(TestCustomer customer, String filter, String filterType) {

      var parts = filter.split(";");
      LocalDate customerBirthdate = customer.birthdate();
      switch (filterType) {
        case "before":
          return customerBirthdate.isBefore(LocalDate.parse(parts[1]));
        case "after":
          return customerBirthdate.isAfter(LocalDate.parse(parts[1]));
        case "between":
          LocalDate startDate = LocalDate.parse(parts[1].trim());
          LocalDate endDate = LocalDate.parse(parts[2].trim());
          return (customerBirthdate.isEqual(startDate) || customerBirthdate.isAfter(startDate))
            && (customerBirthdate.isEqual(endDate) || customerBirthdate.isBefore(endDate));
        default:
          return false;
      }
    }

    @Test
    @DisplayName("Sollte spezifische Kunden für Geburtsdatum vor 1991-01-01 enthalten")
    void testGetAllFilterBirthdateBeforeSpecificCustomers() {
      String url = buildUrl(BIRTHDATE, QUERY_BIRTH_DATE_BEFORE);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      List<String> expectedCustomers = Arrays.asList("mark.williams2", "anna.schmidt");
      assertThat(customerResponse._embedded().customers())
        .extracting(TestCustomer::username)
        .containsAnyElementsOf(expectedCustomers);
    }

    //TODO verbessern
//    @Test
//    @DisplayName("Sollte leere Liste für ungültiges Geburtsdatum-Format zurückgeben")
//    void testGetAllFilterInvalidBirthdateFormat() {
//      String url = buildUrl(BIRTHDATE, INVALID_BIRTHDATE_FORMAT);
//      assertBadRequestResponse(url, INVALID_BIRTHDATE_FORMAT);
//    }
  }

  @Nested
  @DisplayName("Tests für Familienstand-Filter")
  class MaritalStatusFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Familienstand {0} zurückgeben")
    @CsvSource({
      MARITAL_STATUS_SINGLE + ", 7",
      MARITAL_STATUS_MARRIED + ", 15",
      MARITAL_STATUS_DIVORCED + ", 3",
      MARITAL_STATUS_WIDOW + ", 2"
    })
    void testGetAllFilterMaritalStatus(String maritalStatus, int expectedSize) {
      String url = buildUrl(MARITAL_STATUS, maritalStatus);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.maritalStatus().getStatus().equals(maritalStatus))
      );
    }

    //TODO varbessern
//    @Test
//    @DisplayName("Sollte leere Liste für ungültigen Familienstand zurückgeben")
//    void testGetAllFilterInvalidMaritalStatus() {
//      String url = buildUrl(MARITAL_STATUS, INVALID);
//      assertBadRequestResponse(url, INVALID);
//    }
  }

  @Nested
  @DisplayName("Tests für Kundenstatus-Filter")
  class CustomerStatusFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Kundenstatus {0} zurückgeben")
    @CsvSource({
      CUSTOMER_STATUS_ACTIVE + ", 21",
      CUSTOMER_STATUS_BLOCKED + ", 2",
      CUSTOMER_STATUS_INACTIVE + ", 3",
      CUSTOMER_STATUS_CLOSED + ", 1"
    })
    void testGetAllFilterCustomerStatus(String customerStatus, int expectedSize) {
      String url = buildUrl(CUSTOMER_STATUS, customerStatus);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> customer.customerState().getState().equals(customerStatus))
      );
    }

    //TODO verbessern und exception werfen
//    @Test
//    @DisplayName("Sollte leere Liste für ungültigen Kundenstatus zurückgeben")
//    void testGetAllFilterInvalidCustomerStatus() {
//      String url = buildUrl(CUSTOMER_STATUS, INVALID);
//      final var customerResponse = getCustomerResponse(testClientProvider.adminClient, url);
//      assertAll(
//        () -> assertThat(customerResponse._embedded()).isNull()
//      );
//    }
  }

  @Nested
  @DisplayName("Tests für Postleitzahl-Filter")
  class ZipCodeFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Postleitzahl {0} zurückgeben")
    @CsvSource({
      QUERY_ZIP_CODE_70374 + ", 3, exact",
      QUERY_ZIP_CODE_Y1000 + ", 1, partial",
      QUERY_ZIP_CODE_KA + ", 2, prefix"
    })
    void testGetAllFilterZipCode(String zipCode, int expectedSize, String matchType) {
      String url = buildUrl(ZIP_CODE, zipCode);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> matchesZipCode(customer.address().zipCode(), zipCode, matchType))
      );
    }

    private boolean matchesZipCode(String actualZipCode, String expectedZipCode, String matchType) {
      switch (matchType) {
        case "exact":
          return actualZipCode.equalsIgnoreCase(expectedZipCode);
        case "partial":
        case "prefix":
          return actualZipCode.toLowerCase().contains(expectedZipCode.toLowerCase());
        default:
          return false;
      }
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierende Postleitzahl zurückgeben")
    void testGetAllFilterNonExistingZipCode() {
      String url = buildUrl(ZIP_CODE, "99999");
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }

  }

  @Nested
  @DisplayName("Tests für Stadt-Filter")
  class CityFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Stadt {0} zurückgeben")
    @CsvSource({
      QUERY_CITY_STUTTGART + ", 3, exact",
      QUERY_CITY_TOK + ", 1, partial"
    })
    void testGetAllFilterCity(String city, int expectedSize, String matchType) {
      String url = buildUrl(CITY, city);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> matchesCity(customer.address().city(), city, matchType))
      );
    }

    private boolean matchesCity(String actualCity, String expectedCity, String matchType) {
      switch (matchType) {
        case "exact":
          return actualCity.equalsIgnoreCase(expectedCity);
        case "partial":
          return actualCity.toLowerCase().contains(expectedCity.toLowerCase());
        default:
          return false;
      }
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierende Stadt zurückgeben")
    void testGetAllFilterNonExistingCity() {
      String url = buildUrl(CITY, "NonExistentCity");
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }

  }

  @Nested
  @DisplayName("Tests für Bundesland-Filter")
  class StateFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Bundesland {0} zurückgeben")
    @CsvSource({
      QUERY_STATE_NEW_SOUTH_WALES + ", 1, exact",
      QUERY_STATE_BA + ", 5, partial"
    })
    void testGetAllFilterState(String state, int expectedSize, String matchType) {
      String url = buildUrl(STATE, state, matchType.equals("exact"));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> matchesState(customer.address().state(), state, matchType))
      );
    }

    private boolean matchesState(String actualState, String expectedState, String matchType) {
      switch (matchType) {
        case "exact":
          return actualState.equalsIgnoreCase(expectedState);
        case "partial":
          return actualState.toLowerCase().contains(expectedState.toLowerCase());
        default:
          return false;
      }
    }

    private String buildUrl(String paramName, String paramValue, boolean preventEncoding) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
        .queryParam(paramName, paramValue);
      return preventEncoding ? builder.build(false).toUriString() : builder.toUriString();
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierendes Bundesland zurückgeben")
    void testGetAllFilterNonExistingState() {
      String url = buildUrl(STATE, "NonExistentState", false);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }
  }

  @Nested
  @DisplayName("Tests für Länder-Filter")
  class CountryFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Land {0} zurückgeben")
    @CsvSource({
      QUERY_COUNTRY_USA + ", 3, exact",
      QUERY_COUNTRY_LAND + ", 11, partial"
    })
    void testGetAllFilterCountry(String country, int expectedSize, String matchType) {
      String url = buildUrl(COUNTRY, country, matchType.equals("exact"));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> matchesCountry(customer.address().country(), country, matchType))
      );
    }

    private boolean matchesCountry(String actualCountry, String expectedCountry, String matchType) {
      switch (matchType) {
        case "exact":
          return actualCountry.equalsIgnoreCase(expectedCountry);
        case "partial":
          return actualCountry.toLowerCase().contains(expectedCountry.toLowerCase());
        default:
          return false;
      }
    }

    private String buildUrl(String paramName, String paramValue, boolean preventEncoding) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
        .queryParam(paramName, paramValue);
      return preventEncoding ? builder.build(false).toUriString() : builder.toUriString();
    }

    @Test
    @DisplayName("Sollte leere Liste für nicht existierendes Land zurückgeben")
    void testGetAllFilterNonExistingCountry() {
      String url = buildUrl(COUNTRY, "NonExistentCountry", false);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNull()
      );
    }

  }


  @Nested
  @DisplayName("Tests für Kontaktoptionen-Filter")
  class ContactOptionsFilterTests {

    @ParameterizedTest(name = "Sollte Kunden für Kontaktoption {0} zurückgeben")
    @CsvSource({
      CONTACT_OPTION_PHONE + ", 20",
      CONTACT_OPTION_EMAIL + ", 22",
      CONTACT_OPTION_LETTER + ", 14",
      CONTACT_OPTION_SMS + ", 8"
    })
    void testGetAllFilterSingleContactOption(String contactOption, int expectedSize) {
      String url = buildUrl(CONTACT_OPTIONS, contactOption);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> hasContactOption(customer, contactOption))
      );
    }

    @ParameterizedTest(name = "Sollte Kunden für mehrere Kontaktoptionen ({0}) zurückgeben")
    @CsvSource({
      CONTACT_OPTION_PHONE + ";" + CONTACT_OPTION_EMAIL + ";" + CONTACT_OPTION_LETTER + ";" + CONTACT_OPTION_SMS + ", 5",
      CONTACT_OPTION_PHONE + ";" + CONTACT_OPTION_EMAIL + ", 17"
    })
    void testGetAllFilterMultipleContactOptions(String contactOptionsString, int expectedSize) {
      List<String> contactOptions = Arrays.asList(contactOptionsString.split(";"));
      String url = buildUrlWithMultipleParams(CONTACT_OPTIONS, contactOptions);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> hasAnyContactOption(customer, contactOptions))
      );
    }

    private boolean hasContactOption(TestCustomer customer, String contactOption) {
      return customer.contactOptions().stream()
        .anyMatch(option -> option.getOption().equalsIgnoreCase(contactOption));
    }

    private boolean hasAnyContactOption(TestCustomer customer, List<String> contactOptions) {
      return customer.contactOptions().stream()
        .anyMatch(option -> contactOptions.contains(option.getOption().toUpperCase()));
    }

    private String buildUrlWithMultipleParams(String paramName, List<String> paramValues) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
      paramValues.forEach(value -> builder.queryParam(paramName, value));
      return builder.toUriString();
    }

//    @Test
//    @DisplayName("Sollte leere Liste für ungültige Kontaktoption zurückgeben")
//    void testGetAllFilterInvalidContactOption() {
//      String url = buildUrl(CONTACT_OPTIONS, INVALID);
//      assertBadRequestResponse(url, INVALID);
//    }

  }

  @Nested
  @DisplayName("Tests für Interessen-Filter")
  class InterestsFilterTests {

    @ParameterizedTest
    @DisplayName("Sollte Kunden für Interesse {0} zurückgeben")
    @CsvSource({
      INTEREST_INVESTMENTS + ", 6",
      INTEREST_TECHNOLOGY_AND_INNOVATION + ", 16"
    })
    void testGetAllFilterSingleInterest(String interest, int expectedSize) {
      String url = buildUrl(INTERESTS, interest);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> hasInterest(customer, interest))
      );
    }

    @ParameterizedTest(name = "Sollte Kunden für mehrere Interessen ({0}) zurückgeben")
    @CsvSource({
      INTEREST_INVESTMENTS + ";" + INTEREST_SAVINGS_AND_FINANCES + ";" + INTEREST_CREDIT_AND_DEBT + ";" +
        INTEREST_BANK_PRODUCTS_AND_SERVICES + ";" + INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING + ";" +
        INTEREST_REAL_ESTATE + ";" + INTEREST_INSURANCE + ";" + INTEREST_SUSTAINABLE_FINANCE + ";" +
        INTEREST_TECHNOLOGY_AND_INNOVATION + ";" + INTEREST_TRAVEL + ", 1",
      INTEREST_INVESTMENTS + ";" + INTEREST_REAL_ESTATE + ", 4"
    })
    void testGetAllFilterMultipleInterests(String interestsString, int expectedSize) {
      List<String> interests = Arrays.asList(interestsString.split(";"));
      String url = buildUrlWithMultipleParams(INTERESTS, interests);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(expectedSize),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer -> hasAnyInterest(customer, interests))
      );
    }

    private boolean hasInterest(TestCustomer customer, String interest) {
      return customer.interests().stream()
        .anyMatch(i -> i.getInterest().equalsIgnoreCase(interest));
    }

    private boolean hasAnyInterest(TestCustomer customer, List<String> interests) {
      return customer.interests().stream()
        .anyMatch(i -> interests.contains(i.getInterest().toUpperCase()));
    }

    private String buildUrlWithMultipleParams(String paramName, List<String> paramValues) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
      paramValues.forEach(value -> builder.queryParam(paramName, value));
      return builder.toUriString();
    }

//    @Test
//    @DisplayName("Sollte leere Liste für ungültiges Interesse zurückgeben")
//    void testGetAllFilterInvalidInterest() {
//      String url = buildUrl(INTERESTS, INVALID);
//      assertBadRequestResponse(url, INVALID);
//    }
  }

  @Nested
  @DisplayName("Tests für kombinierte Filter")
  class CombinedFilterTests {

    @Test
    @DisplayName("Sollte Kunden für Nachname und E-Mail-Filter zurückgeben")
    void testFilterLastNameAndEmail() {
      String url = buildUrl(Map.of(
        LAST_NAME, QUERY_SON,
        EMAIL, QUERY_ICLOUD_COM
      ));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(1),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer ->
            customer.lastName().toLowerCase().contains(QUERY_SON.toLowerCase())
              && customer.email().toLowerCase().endsWith(QUERY_ICLOUD_COM.toLowerCase())
          )
      );
    }

    @Test
    @DisplayName("Sollte Kunden für Abonnement, Geschlecht, Familienstand und Kundenstatus zurückgeben")
    void testFilterSubscribedAndGenderAndMaritalStatusAndCustomerStatus() {
      Map<String, String> params = new HashMap<>();
      params.put(IS_SUBSCRIBED, QUERY_IS_SUBSCRIBED);
      params.put(GENDER, GENDER_FEMALE);
      params.put(MARITAL_STATUS, MARITAL_STATUS_MARRIED);
      params.put(CUSTOMER_STATUS, CUSTOMER_STATUS_ACTIVE);

      String url = buildUrl(params);
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(7),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer ->
            Boolean.parseBoolean(QUERY_IS_SUBSCRIBED) == customer.subscribed()
              && customer.gender().getGender().equals(GENDER_FEMALE)
              && customer.maritalStatus().getStatus().equals(MARITAL_STATUS_MARRIED)
              && customer.customerState().getState().equals(CUSTOMER_STATUS_ACTIVE)
          )
      );
    }

    @Test
    @DisplayName("Sollte Kunden für Geburtsdatum und Bundesland zurückgeben")
    void testFilterBirthdateAndState() {
      LocalDate cutoffDate = LocalDate.parse(QUERY_BIRTH_DATE_AFTER.split(";")[1]);
      String url = buildUrl(Map.of(
        BIRTHDATE, QUERY_BIRTH_DATE_AFTER,
        STATE, QUERY_STATE_BA
      ));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(2),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer ->
            customer.birthdate().isAfter(cutoffDate)
              && customer.address().state().toLowerCase().contains(QUERY_STATE_BA.toLowerCase())
          )
      );
    }

    @Test
    @DisplayName("Sollte Kunden für Land, Kontaktoption, Interesse und Kundenstufe zurückgeben")
    void testFilterCountryAndContactAndInterestAndTier() {
      var url = buildUrl(Map.of(
        COUNTRY, QUERY_COUNTRY_LAND,
        CONTACT_OPTIONS, CONTACT_OPTION_EMAIL,
        INTERESTS, INTEREST_TECHNOLOGY_AND_INNOVATION,
        TIER_LEVEL,  String.valueOf(TIER_LEVEL_3)
      ));
      CustomerResponse customerResponse = getCustomerResponse(testClientProvider.adminClient, url);

      assertAll(
        () -> assertThat(customerResponse._embedded()).isNotNull(),
        () -> assertThat(customerResponse._embedded().customers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(2),
        () -> assertThat(customerResponse._embedded().customers())
          .allMatch(customer ->
            customer.address().country().toLowerCase().contains(QUERY_COUNTRY_LAND.toLowerCase())
              && customer.contactOptions().stream().anyMatch(contact -> contact.getOption().equals(CONTACT_OPTION_EMAIL))
              && customer.interests().stream().anyMatch(interest -> interest.getInterest().equals(INTEREST_TECHNOLOGY_AND_INNOVATION))
              && customer.tierLevel() == Integer.parseInt(String.valueOf(TIER_LEVEL_3))
          )
      );
    }

    private String buildUrl(Map<String, String> params) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
      params.forEach(builder::queryParam);
      return builder.toUriString();
    }

    @Test
    @DisplayName("Sollte NOT_FOUND zurückgeben für Benutzername, Kundenstufe und Interesse Filter ohne Ergebnisse")
    void testFilterUsernameAndTierAndInterest() {
      String url = buildUrl(Map.of(
        USERNAME, QUERY_IVA,
        TIER_LEVEL,  String.valueOf(TIER_LEVEL_2),
        INTERESTS, INTEREST_INVESTMENTS
      ));

      ResponseEntity<CustomerResponse> response = testClientProvider.adminClient.getForEntity(url, CustomerResponse.class);

      assertAll(
        () -> assertThat(response.getBody()._embedded()).isNull()
      );
    }
  }
}
