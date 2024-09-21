package com.gentlecorp.customer;

import com.gentlecorp.customer.model.CustomerResponse;
import com.gentlecorp.customer.model.TestCustomer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGetAll {

  private static final String SCHEMA_HOST = "http://localhost:";

  private static final String QUERY_PARAM_USERNAME = "username";
  private static final String QUERY_PARAM_PREFIX = "prefix";
  private static final String QUERY_PARAM_LAST_NAME = "lastName";
  private static final String QUERY_PARAM_EMAIL = "email";
  private static final String QUERY_PARAM_SUBSCRIBED = "subscribed";
  private static final String QUERY_PARAM_TIER = "tier";
  private static final String QUERY_PARAM_BIRTHDATE = "birthdate";
  private static final String QUERY_PARAM_GENDER = "gender";
  private static final String QUERY_PARAM_MARITAL_STATUS = "maritalStatus";
  private static final String QUERY_PARAM_CUSTOMER_STATUS = "customerStatus";
  private static final String QUERY_PARAM_ZIP_CODE = "zipCode";
  private static final String QUERY_PARAM_CITY = "city";
  private static final String QUERY_PARAM_STATE = "state";
  private static final String QUERY_PARAM_COUNTRY = "country";
  private static final String QUERY_PARAM_CONTACT = "contact";
  private static final String QUERY_PARAM_INTEREST = "interest";

  private static final String USERNAME = "leroy135";
  private static final String PARTIAL_USERNAME = "son";

  private static final String PREFIX_IVA = "iva";
  private static final String PREFIX_G = "g";

  private static final String LAST_NAME_M = "m";
  private static final String LAST_NAME_SON = "son";

  private static final String PARTIAL_EMAIL = "ivanov";
  private static final String EMAIL = "caleb_g@outlook.de";
  // partial
  private static final String EMAIL_HOST = "icloud.com";

  private static final String IS_SUBSCRIBED = "true";
  private static final String IS_NOT_SUBSCRIBED = "false";

  private static final String TIER_1 = "1";
  private static final String TIER_2 = "2";
  private static final String TIER_3 = "3";
  // Alle vor dem 01.01.1990
  private static final String BIRTH_DATE_BEFORE = "before,1991-01-01";
  // Alle nach dem 01.01.1999
  private static final String BIRTH_DATE_AFTER = "after,1999-01-01";
  // Alle zwischen dem 01.01.1990 und dem 31.12.1998
  private static final String BIRTH_DATE_BETWEEN = "between,1991-01-01,1998-12-31";

  private static final String GENDER_FEMALE = "F";
  private static final String GENDER_MALE = "M";
  private static final String GENDER_DIVERSE = "D";

  private static final String MARITAL_STATUS_SINGLE = "S";
  private static final String MARITAL_STATUS_MARRIED = "M";
  private static final String MARITAL_STATUS_DIVORCED = "D";
  private static final String MARITAL_STATUS_WIDOW = "W";

  private static final String CUSTOMER_STATUS_ACTIVE = "A";
  private static final String CUSTOMER_STATUS_INACTIVE = "I";
  private static final String CUSTOMER_STATUS_CLOSED = "C";
  private static final String CUSTOMER_STATUS_BLOCKED = "B";

  private static final String ZIP_CODE_70374 = "70374";
  // partial
  private static final String ZIP_CODE_Y1000 = "Y1000";
  //prefix
  private static final String ZIP_CODE_KA = "KA";

  private static final String CITY_STUTTGART = "Stuttgart";
  private static final String CITY_KUMASI = "kumasi";
  private static final String CITY_PARTIAL_TOK = "tok";

  private static final String STATE_NEW_SOUTH_WALES = "New South Wales";
  private static final String STATE_PARTIAL_BA = "Ba";

  private static final String COUNTRY_USA = "USA";
  private static final String COUNTRY_PARTIAL_LAND = "land";

  private static final String CONTACT_PHONE = "P";
  private static final String CONTACT_EMAIL = "E";
  private static final String CONTACT_LETTER = "L";
  private static final String CONTACT_SMS = "S";

  private static final String INTEREST_INVESTMENTS = "I";
  private static final String INTEREST_SAVINGS_AND_FINANCES = "SF";
  private static final String INTEREST_CREDIT_AND_DEBT = "CD";
  private static final String INTEREST_BANK_PRODUCTS_AND_SERVICES = "BPS";
  private static final String INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING = "FEC";
  private static final String INTEREST_REAL_ESTATE = "RE";
  private static final String INTEREST_INSURANCE = "IN";
  private static final String INTEREST_SUSTAINABLE_FINANCE = "SUF";
  private static final String INTEREST_TECHNOLOGY_AND_INNOVATION = "IT";
  private static final String INTEREST_TRAVEL = "T";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private Clients clients;

  /*************************************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                S U C H E   N A C H   A L L E N   K U N D E N
   ************************************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllAsAdmin() {
    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(27);  // Überprüft, ob die Liste genau 27 Einträge hat
  }

  @Test
  void testGetAllAsUser() {
    ResponseEntity<CustomerResponse> response = clients.userClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(27);  // Überprüft, ob die Liste genau 27 Einträge hat
  }

  @Test
  void testGetAllAsSupreme() {
    ResponseEntity<CustomerResponse> response = clients.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetAllAsElite() {
    ResponseEntity<CustomerResponse> response = clients.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetAllAsBasic() {
    ResponseEntity<CustomerResponse> response = clients.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetAllAsVisitor() {
    ResponseEntity<CustomerResponse> response =this.restTemplate.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH, CustomerResponse.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  /**********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                S U C H E   N A C H   A L L E N   K U N D E N   M I T   F I L T E R
   **********************************************************************************************************************************************************************************************************************************************************************/

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      U S E R N A M E
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterUsername() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_USERNAME, USERNAME)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);  // Überprüft, ob die Liste genau 1 Eintrag hat

    TestCustomer customer = customerResponse._embedded().customers().getFirst();
    assertThat(customer.username()).isEqualTo(USERNAME);
  }

  @Test
  void testGetAllFilterPartialUsername() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_USERNAME, PARTIAL_USERNAME)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen den Teilstring enthalten
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.username().contains(PARTIAL_USERNAME));

    // Optional: Überprüfen Sie den ersten Benutzer genauer
    TestCustomer firstCustomer = customerResponse._embedded().customers().getFirst();
    assertThat(firstCustomer.username()).contains(PARTIAL_USERNAME);
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      P R E F I X   ( N A C H N A M E )
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterPrefix_IVA() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_PREFIX, PREFIX_IVA)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.lastName().toLowerCase().startsWith(PREFIX_IVA.toLowerCase()));

    // Optional: Überprüfen Sie den ersten Benutzer genauer
    TestCustomer firstCustomer = customerResponse._embedded().customers().getFirst();
    assertThat(firstCustomer.lastName().toLowerCase()).startsWith(PREFIX_IVA.toLowerCase());

    // Optional: Überprüfen Sie auch den zweiten Benutzer
    TestCustomer secondCustomer = customerResponse._embedded().customers().get(1);
    assertThat(secondCustomer.lastName().toLowerCase()).startsWith(PREFIX_IVA.toLowerCase());
  }

  @Test
  void testGetAllFilterPrefix_G() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_PREFIX, PREFIX_G)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(4);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.lastName().toLowerCase().startsWith(PREFIX_G.toLowerCase()));

    // Optional: Überprüfen Sie den ersten Benutzer genauer
    TestCustomer firstCustomer = customerResponse._embedded().customers().getFirst();
    assertThat(firstCustomer.lastName().toLowerCase()).startsWith(PREFIX_G.toLowerCase());
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      N A C H N A M E
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterLastName_M() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_LAST_NAME, LAST_NAME_M)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(11);

    // Überprüfen Sie, ob alle zurückgegebenen Nachnamen "M" oder "m" enthalten
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.lastName().toLowerCase().contains(LAST_NAME_M.toLowerCase()));

    // Optional: Überprüfen Sie einige spezifische Nachnamen
    List<String> expectedLastNames = Arrays.asList("Meyer", "Müller", "Mustermann");
    assertThat(customerResponse._embedded().customers())
      .extracting(TestCustomer::lastName)
      .anyMatch(expectedLastNames::contains);
  }

  @Test
  void testGetAllFilterLastName_SON() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_LAST_NAME, LAST_NAME_SON)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(4);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.lastName().toLowerCase().contains(LAST_NAME_SON.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     E M A I L
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterEmail() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_EMAIL, EMAIL)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    TestCustomer firstCustomer = customerResponse._embedded().customers().getFirst();
    assertThat(firstCustomer.email().toLowerCase()).isEqualTo(EMAIL.toLowerCase());
  }

  @Test
  void testGetAllFilterPartialEmail() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_EMAIL, PARTIAL_EMAIL)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.email().toLowerCase().contains(PARTIAL_EMAIL.toLowerCase()));
  }

  @Test
  void testGetAllFilterEmailHost() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_EMAIL, EMAIL_HOST)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.email().toLowerCase().contains(EMAIL_HOST.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     S U B S C R I P T I O N
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterSubscribed() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_SUBSCRIBED, IS_SUBSCRIBED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(23);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(TestCustomer::subscribed);
  }

  @Test
  void testGetAllFilterNotSubscribed() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_SUBSCRIBED, IS_NOT_SUBSCRIBED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(4);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> !customer.subscribed());
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     T I E R
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterTier1() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_TIER, TIER_1)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(9);

    // Überprüfen Sie, ob alle zurückgegebenen Kunden Tier 1 sind
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.tierLevel() == Integer.parseInt(TIER_1));

    // Optional: Überprüfen Sie, ob bekannte Tier 1 Kunden in der Liste sind
    List<String> expectedTier1Customers = Arrays.asList("julia", "erik", "john.muller");
    assertThat(customerResponse._embedded().customers())
      .extracting(TestCustomer::username)
      .containsAnyElementsOf(expectedTier1Customers);
  }

  @Test
  void testGetAllFilterTier2() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_TIER, TIER_2)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(9);

    // Überprüfen Sie, ob alle zurückgegebenen Kunden Tier 1 sind
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.tierLevel() == Integer.parseInt(TIER_2));
  }

  @Test
  void testGetAllFilterTier3() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_TIER, TIER_3)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(9);

    // Überprüfen Sie, ob alle zurückgegebenen Kunden Tier 1 sind
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.tierLevel() == Integer.parseInt(TIER_3));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     B I R T H D A T E
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterBirthdateBefore() {
    LocalDate cutoffDate = LocalDate.parse(BIRTH_DATE_BEFORE.split(",")[1]);
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_BIRTHDATE, BIRTH_DATE_BEFORE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(18);

    // Überprüfen Sie, ob alle zurückgegebenen Kunden ein Geburtsdatum vor dem Stichtagsdatum haben
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.birthDate().isBefore(cutoffDate));

    // Optional: Überprüfen Sie einige spezifische Kunden
    List<String> expectedCustomers = Arrays.asList("mark.williams2", "anna.schmidt");
    assertThat(customerResponse._embedded().customers())
      .extracting(TestCustomer::username)
      .containsAnyElementsOf(expectedCustomers);
  }

  @Test
  void testGetAllFilterBirthdateAfter() {
    LocalDate cutoffDate = LocalDate.parse(BIRTH_DATE_AFTER.split(",")[1]);
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_BIRTHDATE, BIRTH_DATE_AFTER)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(4);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.birthDate().isAfter(cutoffDate));
  }

  @Test
  void testGetAllFilterBirthdateBetween() {
    String[] dates = BIRTH_DATE_BETWEEN.split(",");
    LocalDate startDate = LocalDate.parse(dates[1].trim());
    LocalDate endDate = LocalDate.parse(dates[2].trim());

    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_BIRTHDATE, BIRTH_DATE_BETWEEN)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(5);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        (customer.birthDate().isEqual(startDate) || customer.birthDate().isAfter(startDate))
          &&
          (customer.birthDate().isEqual(endDate) || customer.birthDate().isBefore(endDate))
      );
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     G E N D E R
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterGenderMale() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_GENDER, GENDER_MALE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(13);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.gender().getGender().equals(GENDER_MALE));
  }

  @Test
  void testGetAllFilterGenderFemale() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_GENDER, GENDER_FEMALE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(11);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.gender().getGender().equals(GENDER_FEMALE));
  }

  @Test
  void testGetAllFilterGenderDiverse() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_GENDER, GENDER_DIVERSE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.gender().getGender().equals(GENDER_DIVERSE));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     M A R I T A L   S T A T U S
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterMaritalStatusSingle() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_MARITAL_STATUS, MARITAL_STATUS_SINGLE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(7);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.maritalStatus().getStatus().equals(MARITAL_STATUS_SINGLE));
  }

  @Test
  void testGetAllFilterMaritalStatusMarried() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_MARITAL_STATUS, MARITAL_STATUS_MARRIED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(15);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.maritalStatus().getStatus().equals(MARITAL_STATUS_MARRIED));
  }

  @Test
  void testGetAllFilterMaritalStatusDivorce() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_MARITAL_STATUS, MARITAL_STATUS_DIVORCED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.maritalStatus().getStatus().equals(MARITAL_STATUS_DIVORCED));
  }

  @Test
  void testGetAllFilterMaritalStatusWidowed() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_MARITAL_STATUS, MARITAL_STATUS_WIDOW)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.maritalStatus().getStatus().equals(MARITAL_STATUS_WIDOW));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     C U S T O M E R   S T A T U S
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterCustomerStatusActive() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CUSTOMER_STATUS, CUSTOMER_STATUS_ACTIVE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(21);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.customerState().getState().equals(CUSTOMER_STATUS_ACTIVE));
  }

  @Test
  void testGetAllFilterCustomerStatusBlocked() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CUSTOMER_STATUS, CUSTOMER_STATUS_BLOCKED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.customerState().getState().equals(CUSTOMER_STATUS_BLOCKED));
  }

  @Test
  void testGetAllFilterCustomerStatusInactive() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CUSTOMER_STATUS, CUSTOMER_STATUS_INACTIVE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.customerState().getState().equals(CUSTOMER_STATUS_INACTIVE));
  }

  @Test
  void testGetAllFilterCustomerStatusClosed() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CUSTOMER_STATUS, CUSTOMER_STATUS_CLOSED)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.customerState().getState().equals(CUSTOMER_STATUS_CLOSED));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     Z I P   C O D E
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterZipCode70374() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_ZIP_CODE, ZIP_CODE_70374)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().zipCode().equalsIgnoreCase(ZIP_CODE_70374));
  }

  @Test
  void testGetAllFilterZipCodePartial() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_ZIP_CODE, ZIP_CODE_Y1000)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().zipCode().toLowerCase().contains(ZIP_CODE_Y1000.toLowerCase()));
  }

  @Test
  void testGetAllFilterZipCodePrefix() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_ZIP_CODE, ZIP_CODE_KA)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().zipCode().toLowerCase().contains(ZIP_CODE_KA.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     C I T Y
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterCityStuttgart() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CITY, CITY_STUTTGART)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().city().equalsIgnoreCase(CITY_STUTTGART));
  }

  @Test
  void testGetAllFilterStatePartial_TOK() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CITY, CITY_PARTIAL_TOK)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().city().toLowerCase().contains(CITY_PARTIAL_TOK.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     S T A T E
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterStateNewSouthWales() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_STATE, STATE_NEW_SOUTH_WALES)
      .build(false) // Verhindert die automatische Kodierung
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().state().equalsIgnoreCase(STATE_NEW_SOUTH_WALES));
  }

  @Test
  void testGetAllFilterStatePartial_BA() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_STATE, STATE_PARTIAL_BA)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(5);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().state().toLowerCase().contains(STATE_PARTIAL_BA.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      C O U N T R Y
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterCountry_USA() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_COUNTRY, COUNTRY_USA)
      .build(false) // Verhindert die automatische Kodierung
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(3);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().country().equalsIgnoreCase(COUNTRY_USA));
  }

  @Test
  void testGetAllFilterStatePartialLand() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_COUNTRY, COUNTRY_PARTIAL_LAND)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(11);

    // Überprüfen Sie, ob alle zurückgegebenen Benutzernamen mit dem Präfix beginnen
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.address().country().toLowerCase().contains(COUNTRY_PARTIAL_LAND.toLowerCase()));
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      C O N T A C T   O P T I O N
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterContactOptionPhone() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CONTACT, CONTACT_PHONE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(20);


    // Prüfen, ob alle Kunden Kontaktoptionen haben
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.contactOptions() != null && !customer.contactOptions().isEmpty());

// Prüfen, ob alle Kunden die Telefonoption haben
    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.contactOptions().stream()
        .anyMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(CONTACT_PHONE)));
  }

  @Test
  void testGetAllFilterContactOptionEmail() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CONTACT, CONTACT_EMAIL)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(22);


    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.contactOptions().stream()
        .anyMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(CONTACT_EMAIL)));
  }

  @Test
  void testGetAllFilterContactOptionLetter() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CONTACT, CONTACT_LETTER)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(14);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.contactOptions().stream()
        .anyMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(CONTACT_LETTER)));
  }

  @Test
  void testGetAllFilterContactOptionSMS() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_CONTACT, CONTACT_SMS)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(8);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.contactOptions().stream()
        .anyMatch(contactOption -> contactOption.getOption().equalsIgnoreCase(CONTACT_SMS)));
  }

  @Test
  void testGetAllFilterAllContactOptions() {
    List<String> allContactOptions = Arrays.asList(CONTACT_PHONE, CONTACT_EMAIL, CONTACT_LETTER, CONTACT_SMS);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
    allContactOptions.forEach(option -> builder.queryParam(QUERY_PARAM_CONTACT, option));

    String url = builder.toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(5);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        customer.contactOptions().stream()
          .anyMatch(contactOption ->
            allContactOptions.contains(contactOption.getOption().toUpperCase())
          )
      );
  }

  @Test
  void testGetAllFilterContactOptionsPhoneAndEmail() {
    List<String> allContactOptions = Arrays.asList(CONTACT_PHONE, CONTACT_EMAIL);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
    allContactOptions.forEach(option -> builder.queryParam(QUERY_PARAM_CONTACT, option));

    String url = builder.toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(17);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        customer.contactOptions().stream()
          .anyMatch(contactOption ->
            allContactOptions.contains(contactOption.getOption().toUpperCase())
          )
      );
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      I N T E R E S T S
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testGetAllFilterInterestInvestments() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_INTEREST, INTEREST_INVESTMENTS)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(6);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.interests().stream()
        .anyMatch(interest -> interest.getInterest().equalsIgnoreCase(INTEREST_INVESTMENTS)));
  }

  @Test
  void testGetAllFilterInterestTechnologyAndInnovation() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_INTEREST, INTEREST_TECHNOLOGY_AND_INNOVATION)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(16);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer -> customer.interests().stream()
        .anyMatch(interest -> interest.getInterest().equalsIgnoreCase(INTEREST_TECHNOLOGY_AND_INNOVATION)));
  }

  @Test
  void testGetAllFilterAllInterests() {
    List<String> allInterests = Arrays.asList(
      INTEREST_INVESTMENTS, INTEREST_SAVINGS_AND_FINANCES, INTEREST_CREDIT_AND_DEBT,
      INTEREST_BANK_PRODUCTS_AND_SERVICES, INTEREST_FINANCIAL_EDUCATION_AND_COUNSELING,
      INTEREST_REAL_ESTATE, INTEREST_INSURANCE, INTEREST_SUSTAINABLE_FINANCE,
      INTEREST_TECHNOLOGY_AND_INNOVATION, INTEREST_TRAVEL
    );

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
    allInterests.forEach(interest -> builder.queryParam(QUERY_PARAM_INTEREST, interest));

    String url = builder.toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        customer.interests().stream()
          .anyMatch(interest ->
            allInterests.contains(interest.getInterest().toUpperCase())
          )
      );
  }

  @Test
  void testGetAllFilterInterestsInvestmentsAndRealEstate() {
    List<String> selectedInterests = Arrays.asList(INTEREST_INVESTMENTS, INTEREST_REAL_ESTATE);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH);
    selectedInterests.forEach(interest -> builder.queryParam(QUERY_PARAM_INTEREST, interest));

    String url = builder.toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(4);

    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        customer.interests().stream()
          .anyMatch(interest ->
            selectedInterests.contains(interest.getInterest().toUpperCase())
          )
      );
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                      M I X E D
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testFilterLastNameAndEmail() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_LAST_NAME, LAST_NAME_SON)
      .queryParam(QUERY_PARAM_EMAIL, EMAIL_HOST)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(1);


    assertThat(customerResponse._embedded().customers())
      .allMatch(customer ->
        customer.lastName().toLowerCase().contains(LAST_NAME_SON.toLowerCase())
          && customer.email().toLowerCase().endsWith(EMAIL_HOST.toLowerCase())
      );
  }

  @Test
  void testFilterSubscribedAndGenderAndMaritalStatusAndCustomerStatus() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_SUBSCRIBED, IS_SUBSCRIBED)
      .queryParam(QUERY_PARAM_GENDER, GENDER_FEMALE)
      .queryParam(QUERY_PARAM_MARITAL_STATUS, MARITAL_STATUS_MARRIED)
      .queryParam(QUERY_PARAM_CUSTOMER_STATUS, CUSTOMER_STATUS_ACTIVE)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(7);

    assertThat(customerResponse._embedded().customers())
      .isNotEmpty()
      .allMatch(customer ->
        customer.subscribed()
          && customer.gender().getGender().equals(GENDER_FEMALE)
          && customer.maritalStatus().getStatus().equals(MARITAL_STATUS_MARRIED)
          && customer.customerState().getState().equals(CUSTOMER_STATUS_ACTIVE)
      );
  }

  @Test
  void testFilterBirthdateAndState() {
    LocalDate cutoffDate = LocalDate.parse(BIRTH_DATE_AFTER.split(",")[1]);
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_BIRTHDATE, BIRTH_DATE_AFTER)
      .queryParam(QUERY_PARAM_STATE, STATE_PARTIAL_BA)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    assertThat(customerResponse._embedded().customers())
      .isNotEmpty()
      .allMatch(customer ->
        customer.birthDate().isAfter(cutoffDate)
          && customer.address().state().toLowerCase().contains(STATE_PARTIAL_BA.toLowerCase())
      );
  }

  @Test
  void testFilterCountryAndContactAndInterestAndTier() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_COUNTRY, COUNTRY_PARTIAL_LAND)
      .queryParam(QUERY_PARAM_CONTACT, CONTACT_EMAIL)
      .queryParam(QUERY_PARAM_INTEREST, INTEREST_TECHNOLOGY_AND_INNOVATION)
      .queryParam(QUERY_PARAM_TIER, TIER_3)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    CustomerResponse customerResponse = response.getBody();
    assertThat(customerResponse._embedded()).isNotNull();
    assertThat(customerResponse._embedded().customers())
      .isNotNull()
      .isNotEmpty()
      .hasSize(2);

    assertThat(customerResponse._embedded().customers())
      .isNotEmpty()
      .allMatch(customer ->
        customer.address().country().toLowerCase().contains(COUNTRY_PARTIAL_LAND.toLowerCase())
          && customer.contactOptions().stream().anyMatch(contact -> contact.getOption().equals(CONTACT_EMAIL))
          && customer.interests().stream().anyMatch(interest -> interest.getInterest().equals(INTEREST_TECHNOLOGY_AND_INNOVATION))
          && customer.tierLevel() == Integer.parseInt(TIER_3)
      );
  }

  /***********************************************************************************************************************************************************************************************************************************************************************
   *                                                                                                                     N O N   F I L T E R
   ***********************************************************************************************************************************************************************************************************************************************************************/

  @Test
  void testFilterUsernameAndTierAndInterest() {
    String url = UriComponentsBuilder.fromHttpUrl(SCHEMA_HOST + port + CUSTOMER_PATH)
      .queryParam(QUERY_PARAM_USERNAME, PARTIAL_USERNAME)
      .queryParam(QUERY_PARAM_TIER, TIER_2)
      .queryParam(QUERY_PARAM_INTEREST, INTEREST_INVESTMENTS)
      .toUriString();

    ResponseEntity<CustomerResponse> response = clients.adminClient.getForEntity(url, CustomerResponse.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
  }
}

