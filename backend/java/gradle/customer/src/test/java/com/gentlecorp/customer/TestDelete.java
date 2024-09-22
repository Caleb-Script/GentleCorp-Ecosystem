package com.gentlecorp.customer;
import com.gentlecorp.customer.model.TestCustomer;
import com.gentlecorp.customer.model.dto.ContactDTO;
import com.gentlecorp.customer.model.enums.RelationshipType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestDelete {

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
  private static final String RELATIONSHIP = "relationship";
  private static final String WITHDRAWAL_LIMIT = "withdrawalLimit";
  private static final String IS_EMERGENCY_CONTACT = "isEmergencyContact";

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

  private static final String CALEB_ID = "/00000000-0000-0000-0000-000000000025";
  private static final String ANNA_ID = "/00000000-0000-0000-0000-000000000024";
  private static final String HEADER_IF_MATCH = "If-Match";
  private static final String ETAG_VALUE_0 = "\"0\"";
  private static final String ETAG_VALUE_1 = "\"1\"";
  private static final String ETAG_VALUE_2 = "\"2\"";


  private static final String CONTACT_LAST_NAME = "Rolly";
  private static final String CONTACT_FIRST_NAME = "Hola";
  private static final String CONTACT_RELATIONSHIP = "S";
  private static final int CONTACT_WITHDRAWAL_LIMIT = 50;
  private static final boolean CONTACT_IS_EMERGENCY = false;

  private static final String EXISTING_CONTACT_LAST_NAME = "Andersson";
  private static final String EXISTING_CONTACT_FIRST_NAME = "Eric";
  private static final String EXISTING_CONTACT_RELATIONSHIP = "S";
  private static final int EXISTING_CONTACT_WITHDRAWAL_LIMIT = 50;
  private static final boolean EXISTING_CONTACT_IS_EMERGENCY = false;


  private static final String INVALID_CONTACT_FIRST_NAME = "";
  private static final String INVALID_CONTACT_LAST_NAME = "";
  private static final String INVALID_CONTACT_RELATIONSHIP = "";
  private static final int INVALID_CONTACT_WITHDRAWAL_LIMIT = -1;
  private static final Boolean INVALID_CONTACT_IS_EMERGENCY = null;
  private final static String ADMIN_ID = "/00000000-0000-0000-0000-000000000000";
  private final static String CONTTACT_R_ID = "/00000000-0000-0000-0000-000000000057";

  @Autowired
  private Clients clients;

  @Autowired
  private TestConfig testConfig;

  @LocalServerPort
  private int port;

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

  private String extractIdFromLocationHeader(URI locationUri) {
    String path = locationUri.getPath();
    return path.substring(path.lastIndexOf('/') + 1);
  }

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

  void deleteAndVerifyCustomer3(String customerId) {
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_2);
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

  private Map<String, Object> createContactBody() {
    Map<String, Object> contactBody = new HashMap<>();
    contactBody.put(LAST_NAME, CONTACT_LAST_NAME);
    contactBody.put(FIRST_NAME, CONTACT_FIRST_NAME);
    contactBody.put(RELATIONSHIP, CONTACT_RELATIONSHIP);
    contactBody.put(WITHDRAWAL_LIMIT, CONTACT_WITHDRAWAL_LIMIT);
    contactBody.put(IS_EMERGENCY_CONTACT, CONTACT_IS_EMERGENCY);
    return contactBody;
  }

  private Map<String, Object> createExistingContactBody() {
    Map<String, Object> contactBody = new HashMap<>();
    contactBody.put(LAST_NAME, EXISTING_CONTACT_LAST_NAME);
    contactBody.put(FIRST_NAME, EXISTING_CONTACT_FIRST_NAME);
    contactBody.put(RELATIONSHIP, EXISTING_CONTACT_RELATIONSHIP);
    contactBody.put(WITHDRAWAL_LIMIT, EXISTING_CONTACT_WITHDRAWAL_LIMIT);
    contactBody.put(IS_EMERGENCY_CONTACT, EXISTING_CONTACT_IS_EMERGENCY);
    return contactBody;
  }

  private Map<String, Object> createInvalidContactBody() {
    Map<String, Object> invalidContactBody = new HashMap<>();
    invalidContactBody.put(LAST_NAME, INVALID_CONTACT_LAST_NAME);
    invalidContactBody.put(FIRST_NAME, INVALID_CONTACT_FIRST_NAME);
    invalidContactBody.put(RELATIONSHIP, INVALID_CONTACT_RELATIONSHIP);
    invalidContactBody.put(WITHDRAWAL_LIMIT, INVALID_CONTACT_WITHDRAWAL_LIMIT);
    invalidContactBody.put(IS_EMERGENCY_CONTACT, INVALID_CONTACT_IS_EMERGENCY);
    return invalidContactBody;
  }

//  private HttpHeaders createHeaders(TestRestTemplate requests, String etagValue) {
//
//    requests.getRestTemplate().getInterceptors().add((request, body, execution) -> {
//      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_1);
//      request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//      return execution.execute(request, body);
//    });
//
//
//    request.getHeaders().set(HEADER_IF_MATCH, etagValue.replaceAll("\"", ""));
//    return headers;
//  }


    @Test
void testDeleteContactAsCustomer() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
        HttpMethod.POST,
        new HttpEntity<>(contactBody),
        Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

      customerClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_MATCH, ETAG_VALUE_0);
      request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      return execution.execute(request, body);
    });
    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = customerClient.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
        HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
        Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

//    // Versuchen Sie, den gelöschten Kontakt abzurufen
//    ResponseEntity<ProblemDetail> getDeletedContactResponse = customerClient.getForEntity(
//        SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
//        ProblemDetail.class
//    );
//
//    assertThat(getDeletedContactResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    // Aufräumen
    deleteAndVerifyCustomer3(customerId);
}


  @Test
  void testDeleteContactAsAdmin() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = clients.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
      HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    deleteAndVerifyCustomer3(customerId);
  }

  // TODO verbessern mit problemDetail
  // TODO test hinzufügen mit versionen
  @Test
  void testDeleteContactAsUser() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = clients.userClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
      HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testDeleteContactAsASupreme() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = clients.supremeClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
      HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testDeleteContactAsElite() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = clients.eliteClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
      HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testDeleteContactAsBasic() {
    // Erstellen Sie einen Kunden
    String customerId = createCustomer(BASIC_USERNAME_VALUE, TIER_LEVEL_VALUE_1);

    // Fügen Sie einen Kontakt hinzu
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME_VALUE, PASSWORD_VALUE);

    ResponseEntity<Void> addContactResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      Void.class
    );

    assertThat(addContactResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    URI contactLocation = addContactResponse.getHeaders().getLocation();
    assertThat(contactLocation).isNotNull();

    String contactId = extractIdFromLocationHeader(contactLocation);

    // Löschen Sie den Kontakt
    ResponseEntity<Void> deleteResponse = clients.basicClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + "/" + customerId + "/" + contactId,
      HttpMethod.DELETE,
      new HttpEntity<>(null, new HttpHeaders()),
      Void.class
    );

    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    deleteAndVerifyCustomer2(customerId);
  }

}
