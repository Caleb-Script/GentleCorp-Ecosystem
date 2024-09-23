package com.gentlecorp.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;


public class TestPatch extends CustomerCommonFunctions {

//  @Test
//  void testCreateContactAsCustomer() {
//    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
//    Map<String, Object> contactBody = createContactBody();
//    var customerClient = testConfig.createAuthenticatedClient(BASIC_USERNAME, PASSWORD_VALUE);
//
//    ResponseEntity<Void> patchResponse = customerClient.exchange(
//      SCHEMA_HOST + port + CUSTOMER_PATH + "/contact/" + customerId,
//      HttpMethod.POST,
//      new HttpEntity<>(contactBody, createHeaders(ETAG_VALUE_1)),
//      Void.class
//    );
//
//    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//    assertThat(patchResponse.getHeaders().getLocation()).isNotNull();
//
//    deleteAndVerifyCustomer(customerId);
//  }



  private static final String CONTACT_URL_TEMPLATE = SCHEMA_HOST + "%d" + CUSTOMER_PATH + CONTACT_PATH + "%s";

  @ParameterizedTest(name = "Add contact when role is {0}")
  @CsvSource({
    ADMIN,
    USER,
    SUPREME,
    ELITE,
    BASIC
  })
  void testAddContactAsRole(final String role) {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> contactBody = createContactBody();
    var client = getClient(role);

    if (role.equals(SUPREME) || role.equals(ELITE) || role.equals(BASIC)) {
      var patchResponse = client.exchange(
        String.format(CONTACT_URL_TEMPLATE, port, customerId),
        HttpMethod.POST,
        new HttpEntity<>(contactBody, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
        ProblemDetail.class
      );
      assertForbidden(patchResponse, role, customerId);
    } else {
      ResponseEntity<Void> patchResponse = client.exchange(
        String.format(CONTACT_URL_TEMPLATE, port, customerId),
        HttpMethod.POST,
        new HttpEntity<>(contactBody, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
        Void.class
      );
      assertCreated(patchResponse, customerId);
    }
  }

  @Test
  void testAddContactAsCustomer() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);

    ResponseEntity<Void> patchResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + CONTACT_PATH + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      Void.class
    );

    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testCreateContactWithoutVersion() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> contactBody = createContactBody();
    var customerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);

    var patchResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + CONTACT_PATH + customerId,
      HttpMethod.POST,
      new HttpEntity<>(contactBody),
      ProblemDetail.class
    );

    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_REQUIRED);
    assertThat(Objects.requireNonNull(patchResponse.getBody()).getDetail()).contains("Versionsnummer fehlt");


    deleteAndVerifyCustomer(customerId);

  }

  @Test
  void testCreateExistingContact() {
    Map<String, Object> contactBody = createExistingContactBody();

    ResponseEntity<ProblemDetail> patchResponse = testClientProvider.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + CONTACT_PATH + ID_ANNA,
      HttpMethod.POST,
      new HttpEntity<>(contactBody, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      ProblemDetail.class
    );

    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(Objects.requireNonNull(patchResponse.getBody()).getDetail()).contains("Der Kontakt: Andersson Eric ist bereits in deiner Liste");
  }

  @Test
  void testCreateContactWithInvalidData() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> invalidContactBody = createInvalidContactBody();

    ResponseEntity<ProblemDetail> patchResponse = testClientProvider.adminClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + CONTACT_PATH + customerId,
      HttpMethod.POST,
      new HttpEntity<>(invalidContactBody, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      ProblemDetail.class
    );

    assertThat(patchResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(Objects.requireNonNull(patchResponse.getBody()).getDetail()).contains("'firstName': First name format is invalid.");
    assertThat(Objects.requireNonNull(patchResponse.getBody()).getDetail()).contains("'lastName': Last name format is invalid.");
    assertThat(Objects.requireNonNull(patchResponse.getBody()).getDetail()).contains("'relationship': Relationship type is required.");

    deleteAndVerifyCustomer(customerId);
  }
}

