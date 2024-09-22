package com.gentlecorp.customer;

import com.gentlecorp.customer.model.TestCustomer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestUpdate {

  private static final String SCHEMA_HOST = "http://localhost:";

  private static final String CUSTOMER = "customer";
  private static final String LAST_NAME = "lastName";
  private static final String FIRST_NAME = "firstName";
  private static final String EMAIL = "email";
  private static final String PHONE_NUMBER = "phoneNumber";
  private static final String TIER_LEVEL = "tierLevel";
  private static final String IS_SUBSCRIBED = "isSubscribed";
  private static final String BIRTH_DATE = "birthDate";
  private static final String GENDER = "gender";
  private static final String MARITAL_STATUS = "maritalStatus";
  private static final String INTERESTS = "interests";
  private static final String CONTACT_OPTIONS = "contactOptions";
  private static final String ADDRESS = "address";
  private static final String STREET = "street";
  private static final String HOUSE_NUMBER = "houseNumber";
  private static final String ZIP_CODE = "zipCode";
  private static final String CITY = "city";
  private static final String STATE = "state";
  private static final String COUNTRY = "country";
  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";

  // Neue Konstanten für die spezifischen Werte
  private static final String LAST_NAME_VALUE = "Gyamfi";
  private static final String FIRST_NAME_VALUE = "Caleb";
  private static final String EMAIL_VALUE = "supreme@ok.de";
  private static final String PHONE_NUMBER_VALUE = "015111951223";
  private static final int TIER_LEVEL_VALUE_3 = 3;
  private static final int TIER_LEVEL_VALUE_2 = 2;
  private static final int TIER_LEVEL_VALUE_1 = 1;
  private static final int IS_SUBSCRIBED_VALUE = 1;
  private static final String BIRTH_DATE_VALUE = "1999-05-03";
  private static final String GENDER_VALUE = "M";
  private static final String MARITAL_STATUS_VALUE = "S";
  private static final String INTERESTS_VALUE = "IN";
  private static final String CONTACT_OPTIONS_VALUE = "S";
  private static final String STREET_VALUE = "Namurstraße";
  private static final String HOUSE_NUMBER_VALUE = "4";
  private static final String ZIP_CODE_VALUE = "70374";
  private static final String CITY_VALUE = "Stuttgart";
  private static final String STATE_VALUE = "Baden-Württemberg";
  private static final String COUNTRY_VALUE = "Germany";
  private static final String SUPREME_USERNAME_VALUE = "gentlecg99_supreme";
  private static final String ELITE_USERNAME_VALUE = "gentlecg99_supreme";
  private static final String BASIC_USERNAME_VALUE = "gentlecg99_supreme";
  private static final String PASSWORD_VALUE = "Caleb123.";


  private static final String HIROSHI_ID = "/00000000-0000-0000-0000-000000000018";
  private static final String CALEB_ID = "/00000000-0000-0000-0000-000000000025";
  private static final String CALEB_CONTACT_ID_1 = "/20000000-0000-0000-0000-000000000000";

  private static final String UPDATED_LAST_NAME = "Updatedastame";
  private static final String UPDATED_FIRST_NAME = "Updatedirstame";
  private static final String UPDATED_USERNAME = "Updatedirstadme";
  private static final String UPDATED_EMAIL = "updated.email@example.com";
  private static final String UPDATED_PHONE_NUMBER = "+49 987 654321";
  private static final int UPDATED_TIER_LEVEL = 2;
  private static final boolean UPDATED_IS_SUBSCRIBED = false;
  private static final String UPDATED_BIRTH_DATE = "1990-01-01";
  private static final String UPDATED_GENDER = "F";
  private static final String UPDATED_MARITAL_STATUS = "M";
  private static final String UPDATED_INTEREST = "IT";
  private static final String UPDATED_CONTACT_OPTION = "S";
  private static final String UPDATED_STREET = "Updated Street";
  private static final String UPDATED_HOUSE_NUMBER = "10B";
  private static final String UPDATED_ZIP_CODE = "54321";
  private static final String UPDATED_CITY = "Updated City";
  private static final String UPDATED_STATE = "Updated State";
  private static final String UPDATED_COUNTRY = "Updated Country";

  private static final String ETAG_VALUE_MINUS_1 = "\"-1\"";
  private static final String ETAG_VALUE_0 = "\"0\"";
  private static final String ETAG_VALUE_1 = "\"1\"";
  private static final String ETAG_VALUE_2 = "\"2\"";
  private static final String ETAG_VALUE_3 = "\"3\"";
  private static final String INVALID_ETAG_VALUE = "\"3";

  private static final String PASSWORD_PATH = "/password";
  private static final String NEW_PASSWORD = "123.Caleb";
  private static final String NEW_INVALID_PASSWORD = "p";

  private static final String HEADER_IF_MATCH = "If-Match";

  @Autowired
  private Clients clients;

  @Autowired
  private TestConfig testConfig;

  @LocalServerPort
  private int port;



  private Map<String, Object> createUpdatePassword(final String password) {
    Map<String, Object> updateRequest = new HashMap<>();
    updateRequest.put(PASSWORD, password);
    return updateRequest;
  }

  private Map<String, Object> createUpdateRequestBody() {
    Map<String, Object> updateRequest = new HashMap<>();

    updateRequest.put(LAST_NAME, UPDATED_LAST_NAME);
    updateRequest.put(FIRST_NAME, UPDATED_FIRST_NAME);
    updateRequest.put(USERNAME, UPDATED_USERNAME);
    updateRequest.put(EMAIL, UPDATED_EMAIL);
    updateRequest.put(PHONE_NUMBER, UPDATED_PHONE_NUMBER);
    updateRequest.put(TIER_LEVEL, UPDATED_TIER_LEVEL);
    updateRequest.put(IS_SUBSCRIBED, UPDATED_IS_SUBSCRIBED);
    updateRequest.put(BIRTH_DATE, UPDATED_BIRTH_DATE);
    updateRequest.put(GENDER, UPDATED_GENDER);
    updateRequest.put(MARITAL_STATUS, UPDATED_MARITAL_STATUS);
    updateRequest.put(INTERESTS, List.of(UPDATED_INTEREST));
    updateRequest.put(CONTACT_OPTIONS, List.of(UPDATED_CONTACT_OPTION));

    Map<String, Object> address = new HashMap<>();
    address.put(STREET, UPDATED_STREET);
    address.put(HOUSE_NUMBER, UPDATED_HOUSE_NUMBER);
    address.put(ZIP_CODE, UPDATED_ZIP_CODE);
    address.put(CITY, UPDATED_CITY);
    address.put(STATE, UPDATED_STATE);
    address.put(COUNTRY, UPDATED_COUNTRY);
    updateRequest.put(ADDRESS, address);

    return updateRequest;
  }

  private Map<String, Object> createContactRequestBody() {
    Map<String, Object> updateRequest = new HashMap<>();

    updateRequest.put(LAST_NAME, UPDATED_LAST_NAME);
    updateRequest.put(FIRST_NAME, UPDATED_FIRST_NAME);
    updateRequest.put("relationship","PN");
    updateRequest.put("withdrawalLimit", 50);
    updateRequest.put("isEmergencyContact", true);

    return updateRequest;
  }

  private void verifyUpdatedCustomer(TestCustomer customer) {
    assertThat(customer.lastName()).isEqualTo(UPDATED_LAST_NAME);
    assertThat(customer.firstName()).isEqualTo(UPDATED_FIRST_NAME);
    assertThat(customer.email()).isEqualTo(UPDATED_EMAIL);
    assertThat(customer.phoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
    assertThat(customer.tierLevel()).isEqualTo(UPDATED_TIER_LEVEL);
    assertThat(customer.subscribed()).isEqualTo(UPDATED_IS_SUBSCRIBED);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.parse(UPDATED_BIRTH_DATE));
    assertThat(customer.gender().getGender()).isEqualToIgnoringCase(UPDATED_GENDER);
    assertThat(customer.maritalStatus().getStatus()).isEqualToIgnoringCase(UPDATED_MARITAL_STATUS);
//    assertThat(customer.interests()).hasSize(1);
//    assertThat(customer.interests().getFirst().getInterest()).isEqualToIgnoringCase(UPDATED_INTEREST);
//    assertThat(customer.contactOptions()).hasSize(1);
//    assertThat(customer.contactOptions().getFirst().getOption()).isEqualToIgnoringCase(UPDATED_CONTACT_OPTION);

//    AddressDTO address = customer.address();
//    assertThat(address.street()).isEqualTo(UPDATED_STREET);
//    assertThat(address.houseNumber()).isEqualTo(UPDATED_HOUSE_NUMBER);
//    assertThat(address.zipCode()).isEqualTo(UPDATED_ZIP_CODE);
//    assertThat(address.city()).isEqualTo(UPDATED_CITY);
//    assertThat(address.state()).isEqualTo(UPDATED_STATE);
//    assertThat(address.country()).isEqualTo(UPDATED_COUNTRY);
  }

  private Map<String, Object> createInvalidUpdateRequestBody() {
    Map<String, Object> updateRequest = createUpdateRequestBody();
    updateRequest.put(EMAIL, "invalid-email");
    updateRequest.put(PHONE_NUMBER, "123"); // Zu kurz
    updateRequest.put(TIER_LEVEL, 10); // Ungültiger Wert
    return updateRequest;
  }

  private String extractIdFromLocationHeader(URI locationUri) {
    String path = locationUri.getPath();
    return path.substring(path.lastIndexOf('/') + 1);
  }

  // Hilfsmethoden für die Erstellung des Request-Bodys und die Überprüfung von Details
  private HttpEntity<Map<String, Object>> createRequestBody(String username, int tierLevel) {
    Map<String, Object> requestBody = new HashMap<>();

    Map<String, Object> customer = new HashMap<>();
    customer.put(LAST_NAME, LAST_NAME_VALUE);
    customer.put(FIRST_NAME, FIRST_NAME_VALUE);
    customer.put(EMAIL, EMAIL_VALUE);
    customer.put(PHONE_NUMBER, PHONE_NUMBER_VALUE);
    customer.put(TIER_LEVEL, tierLevel);
    customer.put(IS_SUBSCRIBED, IS_SUBSCRIBED_VALUE);
    customer.put(BIRTH_DATE, BIRTH_DATE_VALUE);
    customer.put(GENDER, GENDER_VALUE);
    customer.put(MARITAL_STATUS, MARITAL_STATUS_VALUE);
    customer.put(INTERESTS, List.of(INTERESTS_VALUE));
    customer.put(CONTACT_OPTIONS, List.of(CONTACT_OPTIONS_VALUE));
    customer.put(USERNAME, username);

    Map<String, Object> address = new HashMap<>();
    address.put(STREET, STREET_VALUE);
    address.put(HOUSE_NUMBER, HOUSE_NUMBER_VALUE);
    address.put(ZIP_CODE, ZIP_CODE_VALUE);
    address.put(STATE, STATE_VALUE);
    address.put(CITY, CITY_VALUE);
    address.put(COUNTRY, COUNTRY_VALUE);
    customer.put(ADDRESS, address);

    requestBody.put(CUSTOMER, customer);

    Map<String, Object> passwordMap = new HashMap<>();
    passwordMap.put(PASSWORD, PASSWORD_VALUE);
    requestBody.put(PASSWORD, passwordMap);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<>(requestBody, headers);
  }

  void deleteAndVerifyCustomer(String customerId) {
    ResponseEntity<Void> deleteResponse = clients.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      HttpMethod.DELETE,
      null,
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<TestCustomer> getDeletedCustomerResponse = clients.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    );
    assertThat(getDeletedCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  void deleteAndVerifyCustomer2(String customerId) {
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_1);
      return execution.execute(request, body);
    });

    try {
      ResponseEntity<Void> deleteResponse = clients.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
        HttpMethod.DELETE,
        null,
        Void.class
      );
      assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

      ResponseEntity<TestCustomer> getDeletedCustomerResponse = clients.adminClient.getForEntity(
        SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
        TestCustomer.class
      );
      assertThat(getDeletedCustomerResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }

  private String createCustomer(String username, int tierLevel) {
    var request = createRequestBody(username, tierLevel);

    ResponseEntity<Void> response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      Void.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNull();
    assertThat(response.getHeaders().getLocation()).isNotNull();
    assertThat(response.getHeaders().getLocation().toString())
      .startsWith(SCHEMA_HOST + port + CUSTOMER_PATH);

    return extractIdFromLocationHeader(response.getHeaders().getLocation());
  }


  @Test
  void testUpdateNewCustomerAsAdmin() {
    var customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);
    var updateRequest = createUpdateRequestBody();

    ResponseEntity<Void> updateResponse = clients.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      Void.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // Überprüfen Sie die Aktualisierung
    ResponseEntity<TestCustomer> getResponse = clients.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    );

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TestCustomer updatedCustomer = getResponse.getBody();
    assertThat(updatedCustomer).isNotNull();

    verifyUpdatedCustomer(updatedCustomer);

    // Aufräumen
    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testUpdateNewCustomerAsCustomer() {
    // Erstellen Sie zuerst einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Erstellen Sie den Update-Request
    Map<String, Object> updateRequest = createUpdateRequestBody();

    var newCustomerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    // Führen Sie das Update durch
    ResponseEntity<Void> updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      Void.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    // Überprüfen Sie die Aktualisierung
    ResponseEntity<TestCustomer> getResponse = clients.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    );

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TestCustomer updatedCustomer = getResponse.getBody();
    assertThat(updatedCustomer).isNotNull();

    verifyUpdatedCustomer(updatedCustomer);

    // Aufräumen
    deleteAndVerifyCustomer2(customerId);
  }



  @Test
  void testUpdateHiroshiAsUser() {
    var customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);
    var updateRequest = createUpdateRequestBody();

    var updateResponse = clients.userClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Unzureichende RoleType als: USER");

    // Aufräumen
    deleteAndVerifyCustomer(customerId);
  }

  @Test
  void testUpdateHiroshiAsSupreme() {
    var updateRequest = createUpdateRequestBody();

    var updateResponse = clients.supremeClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Unzureichende RoleType als: SUPREME");
  }

  @Test
  void testUpdateHiroshiAsElite() {
    var updateRequest = createUpdateRequestBody();

    var updateResponse = clients.eliteClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Unzureichende RoleType als: ELITE");
  }

  @Test
  void testUpdateHiroshiAsBasic() {
    var updateRequest = createUpdateRequestBody();

    var updateResponse = clients.basicClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Unzureichende RoleType als: BASIC");
  }

  @Test
  void testUpdateCalebContactsAsCaleb() {
    var updateRequest = createContactRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.supremeClient.getRestTemplate().getInterceptors());

    clients.supremeClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_1);
      return execution.execute(request, body);
    });

    try {
      var updateResponse = clients.supremeClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + CALEB_ID + CALEB_CONTACT_ID_1,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest),
        Void.class
      );

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.supremeClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }

  @Test
  void testUpdateCalebContactsAsAdmin() {
    var updateRequest = createContactRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_0);
      return execution.execute(request, body);
    });

    try {
      var updateResponse = clients.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + CALEB_ID + CALEB_CONTACT_ID_1,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest),
        Void.class
      );

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }


  @Test
  void testUpdateHiroshiAsAdminWithOldVersion() {
    var updateRequest = createUpdateRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_MINUS_1);
      return execution.execute(request, body);
    });

    try {
      var updateResponse = clients.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest),
        ProblemDetail.class
      );

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
      ProblemDetail problemDetail = updateResponse.getBody();
      assertThat(problemDetail).isNotNull();
      assertThat(problemDetail.getDetail()).contains("Die Versionsnummer " + ETAG_VALUE_MINUS_1.replaceAll("\"", "") +" ist veraltet.");
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }


  @Test
  void testUpdateCalebAsAdminWithFutureVersion() {
    var updateRequest = createUpdateRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_3);
      return execution.execute(request, body);
    });

    try {
      var updateResponse = clients.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest),
        ProblemDetail.class
      );

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
      ProblemDetail problemDetail = updateResponse.getBody();
      assertThat(problemDetail).isNotNull();
      assertThat(problemDetail.getDetail()).contains("Provided version " + ETAG_VALUE_3.replaceAll("\"", "") + " is ahead of the current version and is not yet applicable.");
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }

  @Test
  void testUpdateHiroshiAsAdminWithoutVersion() {
    var updateRequest = createUpdateRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().remove(HEADER_IF_MATCH);
      return execution.execute(request, body);
    });

    try {
      var updateResponse = clients.adminClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest),
        ProblemDetail.class
      );

      assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
      ProblemDetail problemDetail = updateResponse.getBody();
      assertThat(problemDetail).isNotNull();
      assertThat(problemDetail.getDetail()).contains("Versionsnummer fehlt");
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }

  @Test
  void testUpdateCalebContactsAsAdminWithInvalidVersion() {
    var updateRequest = createUpdateRequestBody();
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, INVALID_ETAG_VALUE);
      return execution.execute(request, body);
    });

    try {

      var updateResponse = clients.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + HIROSHI_ID,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Invalid ETag " + INVALID_ETAG_VALUE);

    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }


  @Test
  void testUpdateCustomerWithInvalidData() {
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);
    Map<String, Object> updateRequest = createInvalidUpdateRequestBody();
    var newCustomerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    var updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("'tierLevel': Tier level must be at most 3.");
    assertThat(problemDetail.getDetail()).contains("'phoneNumber': Please provide a valid phone number.");
    assertThat(problemDetail.getDetail()).contains("'email': Please provide a valid email address.");
    assertThat(problemDetail.getDetail()).contains("'phoneNumber': Phone number must be between 7 and 25 characters long.");

    // Aufräumen
    deleteAndVerifyCustomer(customerId);
  }

  @Test
  void testUpdateNonExistentCustomer() {
    String nonExistentCustomerId = "non-existent-id";
    Map<String, Object> updateRequest = createUpdateRequestBody();

    ResponseEntity<ProblemDetail> updateResponse = clients.basicClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + nonExistentCustomerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("No static resource customer/" + nonExistentCustomerId + ".");
  }

  @Test
  void testUpdateNewCustomerPasswordAsCustomer() {
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);
    Map<String, Object> updateRequest = createUpdatePassword(NEW_PASSWORD);
    var newCustomerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);
    ResponseEntity<Void> updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + PASSWORD_PATH,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      Void.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    var newCustomerNewPasswordClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, NEW_PASSWORD);
    ResponseEntity<TestCustomer> getNewResponse = newCustomerNewPasswordClient .getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    );

    assertThat(getNewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TestCustomer updatedNewCustomer = getNewResponse.getBody();
    assertThat(updatedNewCustomer).isNotNull();

    deleteAndVerifyCustomer(customerId);
  }

  @Test
  void testUpdateNewCustomerInvalidPasswordAsCustomer() {
    var customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);
    var updateRequest = createUpdatePassword(NEW_INVALID_PASSWORD);
    var newCustomerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);
    var updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + PASSWORD_PATH,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Ungueltiges Passwort " + NEW_INVALID_PASSWORD);

    deleteAndVerifyCustomer(customerId);
  }


}
