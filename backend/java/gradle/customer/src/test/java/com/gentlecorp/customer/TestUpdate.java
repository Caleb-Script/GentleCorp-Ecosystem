package com.gentlecorp.customer;

import com.gentlecorp.customer.model.TestCustomer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUpdate extends CustomerCommonFunctions {


  private void performUpdateAndAssertResponse(String clientType, String customerId, Map<String, Object> updateRequest, HttpStatus expectedStatus, String expectedDetail) {
    var client = getClient(clientType);
    ResponseEntity<?> updateResponse;

    if (expectedStatus == HttpStatus.NO_CONTENT) {
      updateResponse = client.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
        Void.class
      );
    } else {
      updateResponse = client.exchange(
        SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
        HttpMethod.PUT,
        new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
        ProblemDetail.class
      );
    }

    assertThat(updateResponse.getStatusCode()).isEqualTo(expectedStatus);
    if (expectedDetail != null) {
      ProblemDetail problemDetail = (ProblemDetail) updateResponse.getBody();
      assertThat(problemDetail).isNotNull();
      assertThat(problemDetail.getDetail()).contains(expectedDetail);
    }
  }

  private void verifyCustomerUpdate(String customerId) {
    ResponseEntity<TestCustomer> getResponse = testClientProvider.adminClient.getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
      TestCustomer.class
    );

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TestCustomer updatedCustomer = getResponse.getBody();
    assertThat(updatedCustomer).isNotNull();
    verifyUpdatedCustomer(updatedCustomer);
  }

  @Test
  void testUpdateNewCustomerAsAdmin() {
    var customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    var updateRequest = createUpdateRequestBody();

    performUpdateAndAssertResponse(ADMIN, customerId, updateRequest, HttpStatus.NO_CONTENT, null);
    verifyCustomerUpdate(customerId);
    deleteAndVerifyCustomer2(customerId);
  }

  @Test
  void testUpdateNewCustomerAsCustomer() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> updateRequest = createUpdateRequestBody();

    var customerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);
    var updateResponse = customerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      Void.class
    );
    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    verifyCustomerUpdate(customerId);
    deleteAndVerifyCustomer2(customerId);
  }

  @ParameterizedTest(name = "Update Hiroshi as {0}")
  @ValueSource(strings = {USER, SUPREME, ELITE, BASIC})
  void testUpdateHiroshiAsRole(String role) {
    var updateRequest = createUpdateRequestBody();
    performUpdateAndAssertResponse(role, ID_HIROSHI, updateRequest, HttpStatus.FORBIDDEN, "Unzureichende RoleType als: " + role.toUpperCase());
  }

  @Test
  void testUpdateCalebContactsAsCaleb() {
    performUpdateWithInterceptor(SUPREME, ID_CALEB + CALEB_CONTACT_ID_1, createContactRequestBody(), ETAG_VALUE_1, HttpStatus.NO_CONTENT, null);
  }

  @Test
  void testUpdateCalebContactsAsAdmin() {
    performUpdateWithInterceptor(ADMIN, ID_CALEB + CALEB_CONTACT_ID_1, createContactRequestBody(), ETAG_VALUE_0, HttpStatus.NO_CONTENT, null);
  }

  @Test
  void testUpdateHiroshiAsAdminWithOldVersion() {
    performUpdateWithInterceptor(ADMIN, ID_HIROSHI, createUpdateRequestBody(), ETAG_VALUE_MINUS_1, HttpStatus.PRECONDITION_FAILED,
      "Die Versionsnummer " + ETAG_VALUE_MINUS_1.replaceAll("\"", "") + " ist veraltet.");
  }

  @Test
  void testUpdateCalebAsAdminWithFutureVersion() {
    performUpdateWithInterceptor(ADMIN, ID_HIROSHI, createUpdateRequestBody(), ETAG_VALUE_3, HttpStatus.PRECONDITION_FAILED,
      "Provided version " + ETAG_VALUE_3.replaceAll("\"", "") + " is ahead of the current version and is not yet applicable.");
  }

  @Test
  void testUpdateHiroshiAsAdminWithoutVersion() {
    performUpdateWithInterceptor(ADMIN, ID_HIROSHI, createUpdateRequestBody(), null, HttpStatus.PRECONDITION_REQUIRED, "Versionsnummer fehlt");
  }

  @Test
  void testUpdateCalebContactsAsAdminWithInvalidVersion() {
    performUpdateWithInterceptor(ADMIN, ID_HIROSHI, createUpdateRequestBody(), INVALID_ETAG_VALUE, HttpStatus.PRECONDITION_REQUIRED,
      "Invalid ETag " + INVALID_ETAG_VALUE);
  }

  private void performUpdateWithInterceptor(String clientType, String customerId, Map<String, Object> updateRequest, String etagValue, HttpStatus expectedStatus, String expectedDetail) {
    var client = getClient(clientType);
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(client.getRestTemplate().getInterceptors());

    client.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      if (etagValue != null) {
        request.getHeaders().set(HEADER_IF_MATCH, etagValue);
      } else {
        request.getHeaders().remove(HEADER_IF_MATCH);
      }
      return execution.execute(request, body);
    });

    try {
      performUpdateAndAssertResponse(clientType, customerId, updateRequest, expectedStatus, expectedDetail);
    } finally {
      client.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }



  @Test
  void testUpdateCustomerWithInvalidData() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> updateRequest = createInvalidUpdateRequestBody();
    var newCustomerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);

    var updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
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

    ResponseEntity<ProblemDetail> updateResponse = testClientProvider.basicClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + nonExistentCustomerId,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("No static resource customer" + nonExistentCustomerId + ".");
  }

  @Test
  void testUpdateNewCustomerPasswordAsCustomer() {
    String customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    Map<String, Object> updateRequest = createUpdatePassword(NEW_PASSWORD);
    var newCustomerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);
    ResponseEntity<Void> updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + PASSWORD_PATH,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      Void.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    var newCustomerNewPasswordClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_PASSWORD);
    ResponseEntity<TestCustomer> getNewResponse = newCustomerNewPasswordClient .getForEntity(
      SCHEMA_HOST + port + CUSTOMER_PATH + customerId,
      TestCustomer.class
    );

    assertThat(getNewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    TestCustomer updatedNewCustomer = getNewResponse.getBody();
    assertThat(updatedNewCustomer).isNotNull();

    deleteAndVerifyCustomer(customerId);
  }

  @Test
  void testUpdateNewCustomerInvalidPasswordAsCustomer() {
    var customerId = createCustomer(BASIC_USERNAME, BASIC_EMAIL, TIER_LEVEL_1);
    var updateRequest = createUpdatePassword(NEW_INVALID_PASSWORD);
    var newCustomerClient = testClientProvider.createAuthenticatedClient(BASIC_USERNAME, NEW_USER_PASSWORD);
    var updateResponse = newCustomerClient.exchange(
      SCHEMA_HOST + port + CUSTOMER_PATH + PASSWORD_PATH,
      HttpMethod.PUT,
      new HttpEntity<>(updateRequest, createHeaders(HEADER_IF_MATCH, ETAG_VALUE_0)),
      ProblemDetail.class
    );

    assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    ProblemDetail problemDetail = updateResponse.getBody();
    assertThat(problemDetail).isNotNull();
    assertThat(problemDetail.getDetail()).contains("Ungueltiges Passwort " + NEW_INVALID_PASSWORD);

    deleteAndVerifyCustomer(customerId);
  }


}
