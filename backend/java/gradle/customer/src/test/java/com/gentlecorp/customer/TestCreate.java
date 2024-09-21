package com.gentlecorp.customer;

import com.gentlecorp.customer.model.HateoasLinks;
import com.gentlecorp.customer.model.TestCustomer;
import com.gentlecorp.customer.model.dto.AddressDTO;
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

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("unchecked")
public class TestCreate {

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

  private static final String INVALID_EMAIL = "kwame.owusuexample.com";
  private static final String EXISTING_EMAIL = "kwame.owusu@example.com";
  private static final String DUPLICATE_USERNAME = "gentlecg99";
  private static final String INVALID_LAST_NAME = "123Invalid";
  private static final String INVALID_FIRST_NAME = "Invalid123";
  private static final String INVALID_PHONE_NUMBER = "123";
  private static final String INVALID_USERNAME = "a";
  private static final int INVALID_TIER_LEVEL = 5;
  private static final String INVALID_GENDER = "INVALID";
  private static final String INVALID_MARITAL_STATUS = "INVALID";

  private static final String ETAG_VALUE_0 = "\"0\"";
  private static final String HEADER_IF_MATCH = "If-Match";

  @Autowired
  private Clients clients;

  @Autowired
  private TestConfig testConfig;

  @LocalServerPort
  private int port;

private String newId;

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

  private String createCustomer( String username, int tierLevel) {
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

  private void verifyCustomerAsAdmin(String customerId, String expectedUsername, int expectedTierLevel) {
    ResponseEntity<TestCustomer> getResponse = clients.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    );
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();

    var createdCustomer = getResponse.getBody();
    verifyCustomerDetails(createdCustomer, expectedUsername,expectedTierLevel);

    verifyAddress(createdCustomer.address());
    verifyInterestsAndContactOptions(createdCustomer);
    verifyLinks(createdCustomer._links(), customerId);
  }

  private void verifyAccessRights(String customerId) {
    assertThat(clients.supremeClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(clients.eliteClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    assertThat(clients.basicClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId,
      TestCustomer.class
    ).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  private void createAndVerifyCustomer(String originalCustomerId, String username, int expectedTierLevel) {
    var newCustomerClient = testConfig.createAuthenticatedClient(username, PASSWORD_VALUE);

    ResponseEntity<TestCustomer> getResponse = newCustomerClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + originalCustomerId,
      TestCustomer.class
    );
    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(getResponse.getBody()).isNotNull();

    var createdCustomer = getResponse.getBody();
    verifyCustomerDetails(createdCustomer, username, expectedTierLevel);

    verifyAddress(createdCustomer.address());
    verifyInterestsAndContactOptions(createdCustomer);
    verifyLinks(createdCustomer._links(), originalCustomerId);
  }

  private void deleteAndVerifyCustomer(String customerId) {
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


  private void verifyAddress(AddressDTO address) {
    assertThat(address).satisfies(addr -> {
      assertThat(addr.street()).isEqualTo(STREET_VALUE);
      assertThat(addr.houseNumber()).isEqualTo(HOUSE_NUMBER_VALUE);
      assertThat(addr.zipCode()).isEqualTo(ZIP_CODE_VALUE);
      assertThat(addr.city()).isEqualTo(CITY_VALUE);
      assertThat(addr.state()).isEqualTo(STATE_VALUE);
      assertThat(addr.country()).isEqualTo(COUNTRY_VALUE);
    });
  }

  private void verifyInterestsAndContactOptions(TestCustomer customer) {
    assertThat(customer.interests())
      .allMatch(interest -> interest.getInterest().equals(INTERESTS_VALUE));

    assertThat(customer.contactOptions())
      .allMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(CONTACT_OPTIONS_VALUE));
  }

  private void verifyLinks(HateoasLinks hateoaslinks, String customerId) {
    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + customerId;
    assertThat(hateoaslinks).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
      assertThat(links.list().href()).isEqualTo(baseUri);
      assertThat(links.add().href()).isEqualTo(baseUri);
      assertThat(links.update().href()).isEqualTo(idUri);
      assertThat(links.remove().href()).isEqualTo(idUri);
    });
  }

  private void verifyCustomerDetails(TestCustomer customer, String expectedUsername, int expectedTierLevel) {
    assertThat(customer.username()).isEqualTo(expectedUsername);
    assertThat(customer.lastName()).isEqualTo(LAST_NAME_VALUE);
    assertThat(customer.firstName()).isEqualTo(FIRST_NAME_VALUE);
    assertThat(customer.email()).isEqualTo(EMAIL_VALUE);
    assertThat(customer.phoneNumber()).isEqualTo(PHONE_NUMBER_VALUE);
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(expectedTierLevel);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.parse(BIRTH_DATE_VALUE));
    assertThat(customer.gender().getGender().toLowerCase()).isEqualTo(GENDER_VALUE.toLowerCase());
    assertThat(customer.maritalStatus().getStatus().toLowerCase()).isEqualTo(MARITAL_STATUS_VALUE.toLowerCase());
  }

  @Test
  void testCreateSupremeCustomer() {
    var username = SUPREME_USERNAME_VALUE;
    var tierLevel = TIER_LEVEL_VALUE_3;

    // 1. Erstellen Sie den Supreme-Kunden
    String newCustomerId = createCustomer(username, tierLevel);

    // 2. Überprüfen Sie den erstellten Kunden als Admin
    verifyCustomerAsAdmin(newCustomerId, username, tierLevel);

    // 3. Überprüfen Sie die Zugriffsrechte
    verifyAccessRights(newCustomerId);

    // 4. Erstellen Sie einen Basic-Kunden mit denselben Daten
    createAndVerifyCustomer(newCustomerId, username, tierLevel);

    // 5. Löschen Sie den Kunden und überprüfen Sie die Löschung
    deleteAndVerifyCustomer(newCustomerId);
  }

  @Test
  void testCreateEliteCustomer() {
    var username = ELITE_USERNAME_VALUE;
    var tierLevel = TIER_LEVEL_VALUE_2;

    // 1. Erstellen Sie den Supreme-Kunden
    String newCustomerId = createCustomer(username, tierLevel);

    // 2. Überprüfen Sie den erstellten Kunden als Admin
    verifyCustomerAsAdmin(newCustomerId, username, tierLevel);

    // 3. Überprüfen Sie die Zugriffsrechte
    verifyAccessRights(newCustomerId);

    // 4. Erstellen Sie einen Basic-Kunden mit denselben Daten
    createAndVerifyCustomer(newCustomerId, username, tierLevel);

    // 5. Löschen Sie den Kunden und überprüfen Sie die Löschung
    deleteAndVerifyCustomer(newCustomerId);
  }

  @Test
  void testCreateBasicCustomer() {
    var username = BASIC_USERNAME_VALUE;
    var tierLevel = TIER_LEVEL_VALUE_1;

    // 1. Erstellen Sie den Supreme-Kunden
    String newCustomerId = createCustomer(username, tierLevel);

    // 2. Überprüfen Sie den erstellten Kunden als Admin
    verifyCustomerAsAdmin(newCustomerId, username, tierLevel);

    // 3. Überprüfen Sie die Zugriffsrechte
    verifyAccessRights(newCustomerId);

    // 4. Erstellen Sie einen Basic-Kunden mit denselben Daten
    createAndVerifyCustomer(newCustomerId, username, tierLevel);

    // 5. Löschen Sie den Kunden und überprüfen Sie die Löschung
    deleteAndVerifyCustomer(newCustomerId);
  }


  @Test
  void testCreateCustomerWithInvalidEmail() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);

    // Ersetzen Sie die E-Mail-Adresse im Request-Body mit der ungültigen E-Mail
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(EMAIL, INVALID_EMAIL);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("'email': Please provide a valid email address.");
  }

  @Test
  void testCreateCustomerWithDuplicateUsername() {
    // Versuchen Sie, einen Kunden mit demselben Benutzernamen zu erstellen
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Der Benutzername " + DUPLICATE_USERNAME + " existiert bereits.");
  }

  @Test
  void testCreateCustomerWithExistingEmail() {
    // Erstellen Sie den ersten Kunden mit der E-Mail-Adresse
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(EMAIL, EXISTING_EMAIL);
    clients.visitorClient.postForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, request, Void.class);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(response.getBody()).isNotNull();
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Die Emailadresse " + EXISTING_EMAIL + " existiert bereits");
  }

  @Test
  void testCreateCustomerWithInvalidLastName() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(LAST_NAME, INVALID_LAST_NAME);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("'lastName': Invalid last name format.");
  }

  @Test
  void testCreateCustomerWithInvalidFirstName() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(FIRST_NAME, INVALID_FIRST_NAME);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("'firstName': First name should only contain letters.");
  }


  @Test
  void testCreateCustomerWithInvalidPhoneNumber() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(PHONE_NUMBER, INVALID_PHONE_NUMBER);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Please provide a valid phone number.");
  }

  @Test
  void testCreateCustomerWithInvalidUsername() {
    HttpEntity<Map<String, Object>> request = createRequestBody(INVALID_USERNAME, TIER_LEVEL_VALUE_1);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    String errorDetail = Objects.requireNonNull(response.getBody()).getDetail();
    assertThat(errorDetail).contains("Username can only contain alphanumeric characters, underscores, dots, or hyphens");
    assertThat(errorDetail).contains("Username must be between 4 and 20 characters long");
  }

  @Test
  void testCreateCustomerWithInvalidTierLevel() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, INVALID_TIER_LEVEL);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Tier level must be at most 3");
  }

  @Test
  void testCreateCustomerWithFutureBirthDate() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(BIRTH_DATE, LocalDate.now().plusDays(1).toString());

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Birthdate must be a past date");
  }

  @Test
  void testCreateCustomerWithInvalidGender() {
    HttpEntity<Map<String, Object>> request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(GENDER, INVALID_GENDER);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("'gender': Please specify your gender.");
  }

  @Test
  void testCreateCustomerWithInvalidMaritalStatus() {
    var request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(MARITAL_STATUS, INVALID_MARITAL_STATUS);

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("'maritalStatus': Please specify your marital status.");
  }

  @Test
  void testCreateCustomerWithDuplicateInterests() {
    var request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(INTERESTS, List.of(INTERESTS_VALUE, INTERESTS_VALUE));

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Interests must be unique");
  }

  @Test
  void testCreateCustomerWithDuplicateContactOptions() {
    var request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).put(CONTACT_OPTIONS, List.of(CONTACT_OPTIONS_VALUE, CONTACT_OPTIONS_VALUE));

    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Contact options must be unique");
  }

  @Test
  void testCreateCustomerWithoutAddress() {
    var request = createRequestBody(DUPLICATE_USERNAME, TIER_LEVEL_VALUE_1);
    ((Map<String, Object>) Objects.requireNonNull(request.getBody()).get(CUSTOMER)).remove(ADDRESS);


    var response = clients.visitorClient.postForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH,
      request,
      ProblemDetail.class
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(Objects.requireNonNull(response.getBody()).getDetail()).contains("Der Benutzername gentlecg99 existiert bereits.");
  }

}
