package com.gentlecorp.customer;

import com.gentlecorp.customer.model.TestCustomer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.InterestType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.enums.RelationshipType;
import com.gentlecorp.customer.model.enums.StatusType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGetById {

  private static final String SCHEMA_HOST = "http://localhost:";

  private static final String HIROSHI_ID = "00000000-0000-0000-0000-000000000018";
  private static final String NOT_EXISTING_ID = "20000000-0000-0000-0000-000000000000";
  private static final String ERIK_ID = "00000000-0000-0000-0000-000000000005";
  private static final String CALEB_ID = "00000000-0000-0000-0000-000000000025";
  private static final String LEROY_ID = "00000000-0000-0000-0000-000000000026";

  private static final String HEADER_IF_NONE_MATCH = "If-None-Match";
  private static final String ETAG_VALUE_0 = "\"0\"";
  private static final String HEADER_IF_MATCH = "If-Match";

  @Autowired
  private Clients clients;

  @LocalServerPort
  private int port;


  @Test
  void testGetHiroshiByIdAsAdmin() {
    ResponseEntity<TestCustomer> response = clients.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("hiroshi.tanaka");
    assertThat(customer.lastName()).isEqualTo("Tanaka");
    assertThat(customer.firstName()).isEqualTo("Hiroshi");
    assertThat(customer.email()).isEqualTo("hiroshi.tanaka@example.com");
    assertThat(customer.phoneNumber()).isEqualTo("+81-3-1234-5678");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(1);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1988, 6, 20));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
      assertThat(address.street()).isEqualTo("Shibuya Crossing");
      assertThat(address.houseNumber()).isEqualTo("1-2-3");
      assertThat(address.zipCode()).isEqualTo("150-0001");
      assertThat(address.city()).isEqualTo("Tokyo");
      assertThat(address.state()).isEqualTo("Kanto");
      assertThat(address.country()).isEqualTo("Japan");
    });

    assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + HIROSHI_ID;
    assertThat(customer._links()).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
      assertThat(links.list().href()).isEqualTo(baseUri);
      assertThat(links.add().href()).isEqualTo(baseUri);
      assertThat(links.update().href()).isEqualTo(idUri);
      assertThat(links.remove().href()).isEqualTo(idUri);
    });
  }

  @Test
  void testGetHiroshiByIdAsUser() {
    var response = clients.userClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("hiroshi.tanaka");
    assertThat(customer.lastName()).isEqualTo("Tanaka");
    assertThat(customer.firstName()).isEqualTo("Hiroshi");
    assertThat(customer.email()).isEqualTo("hiroshi.tanaka@example.com");
    assertThat(customer.phoneNumber()).isEqualTo("+81-3-1234-5678");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(1);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1988, 6, 20));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
      assertThat(address.street()).isEqualTo("Shibuya Crossing");
      assertThat(address.houseNumber()).isEqualTo("1-2-3");
      assertThat(address.zipCode()).isEqualTo("150-0001");
      assertThat(address.city()).isEqualTo("Tokyo");
      assertThat(address.state()).isEqualTo("Kanto");
      assertThat(address.country()).isEqualTo("Japan");
    });

    assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);

    var baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    var idUri = baseUri + "/" + HIROSHI_ID;
    assertThat(customer._links()).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
      assertThat(links.list().href()).isEqualTo(baseUri);
      assertThat(links.add().href()).isEqualTo(baseUri);
      assertThat(links.update().href()).isEqualTo(idUri);
      assertThat(links.remove().href()).isEqualTo(idUri);
    });
  }

  @Test
  void testGetHiroshiByIdAsSupreme() {
    var response = clients.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetHiroshiByIdAsElite() {
    var response = clients.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetHiroshiByIdAsBasic() {
    var response = clients.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetHiroshiByIdAsVisitor() {
    var response = clients.visitorClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testGetCustomerByIdNotFound() {
    var response = clients.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + NOT_EXISTING_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetErikByIdAsErik() {
    var response = clients.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + ERIK_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("erik");
    assertThat(customer.lastName()).isEqualTo("Schmidt");
    assertThat(customer.firstName()).isEqualTo("Erik");
    assertThat(customer.email()).isEqualTo("erik.schmidt@example.com");
    assertThat(customer.phoneNumber()).isEqualTo("030-2345678");
    assertThat(customer.subscribed()).isFalse();
    assertThat(customer.tierLevel()).isEqualTo(1);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1982, 3, 25));
    assertThat(customer.customerState()).isEqualTo(StatusType.INACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
      assertThat(address.street()).isEqualTo("Eichenstraße");
      assertThat(address.houseNumber()).isEqualTo("8");
      assertThat(address.zipCode()).isEqualTo("20255");
      assertThat(address.city()).isEqualTo("Hamburg");
      assertThat(address.state()).isEqualTo("Hamburg");
      assertThat(address.country()).isEqualTo("Deutschland");
    });

    assertThat(customer.interests()).containsExactlyInAnyOrder(InterestType.BANK_PRODUCTS_AND_SERVICES, InterestType.FINANCIAL_EDUCATION_AND_COUNSELING,InterestType.SUSTAINABLE_FINANCE);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.LETTER, ContactOptionsType.SMS);

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + ERIK_ID;
    assertThat(customer._links()).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
      assertThat(links.list().href()).isEqualTo(baseUri);
      assertThat(links.add().href()).isEqualTo(baseUri);
      assertThat(links.update().href()).isEqualTo(idUri);
      assertThat(links.remove().href()).isEqualTo(idUri);
    });
  }

  @Test
  void testGetLeroyByIdAsLeroy() {
    var response = clients.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + LEROY_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("leroy135");
    assertThat(customer.lastName()).isEqualTo("Jefferson");
    assertThat(customer.firstName()).isEqualTo("Leroy");
    assertThat(customer.email()).isEqualTo("leroy135@icloud.com");
    assertThat(customer.phoneNumber()).isEqualTo("015111951223");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(2);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1999, 5, 3));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.SINGLE);

    assertThat(customer.address()).satisfies(address -> {
      assertThat(address.street()).isEqualTo("Connell Street");
      assertThat(address.houseNumber()).isEqualTo("42");
      assertThat(address.zipCode()).isEqualTo("D01 C3N0");
      assertThat(address.city()).isEqualTo("Dublin");
      assertThat(address.state()).isEqualTo("Leinster");
      assertThat(address.country()).isEqualTo("Ireland");
    });

    assertThat(customer.interests()).containsExactlyInAnyOrder(
      InterestType.INVESTMENTS, InterestType.SAVING_AND_FINANCE, InterestType.CREDIT_AND_DEBT,
      InterestType.BANK_PRODUCTS_AND_SERVICES, InterestType.FINANCIAL_EDUCATION_AND_COUNSELING,
      InterestType.REAL_ESTATE, InterestType.INSURANCE, InterestType.SUSTAINABLE_FINANCE,
      InterestType.TECHNOLOGY_AND_INNOVATION, InterestType.TRAVEL
    );
    assertThat(customer.contactOptions()).containsExactly(ContactOptionsType.EMAIL);

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + LEROY_ID;
    assertThat(customer._links()).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
    });
  }

  @Test
  void testGetCalebByIdAsCaleb() {
    var response = clients.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + CALEB_ID, TestCustomer.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("gentlecg99");
    assertThat(customer.lastName()).isEqualTo("Gyamfi");
    assertThat(customer.firstName()).isEqualTo("Caleb");
    assertThat(customer.email()).isEqualTo("caleb_g@outlook.de");
    assertThat(customer.phoneNumber()).isEqualTo("015111951223");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(3);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1999, 5, 3));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
      assertThat(address.street()).isEqualTo("Namurstraße");
      assertThat(address.houseNumber()).isEqualTo("4");
      assertThat(address.zipCode()).isEqualTo("70374");
      assertThat(address.city()).isEqualTo("Stuttgart");
      assertThat(address.state()).isEqualTo("Baden Württemberg");
      assertThat(address.country()).isEqualTo("Deutschland");
    });

    assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(
      ContactOptionsType.EMAIL, ContactOptionsType.PHONE,
      ContactOptionsType.LETTER, ContactOptionsType.SMS
    );

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + CALEB_ID;
    assertThat(customer._links()).satisfies(links -> {
      assertThat(links.self().href()).isEqualTo(idUri);
    });
  }

  @Test
void testGetFullHiroshiByIdAsAdmin() {
    ResponseEntity<TestCustomer> response = clients.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/all/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("hiroshi.tanaka");
    assertThat(customer.lastName()).isEqualTo("Tanaka");
    assertThat(customer.firstName()).isEqualTo("Hiroshi");
    assertThat(customer.email()).isEqualTo("hiroshi.tanaka@example.com");
    assertThat(customer.phoneNumber()).isEqualTo("+81-3-1234-5678");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(1);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1988, 6, 20));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
        assertThat(address.street()).isEqualTo("Shibuya Crossing");
        assertThat(address.houseNumber()).isEqualTo("1-2-3");
        assertThat(address.zipCode()).isEqualTo("150-0001");
        assertThat(address.city()).isEqualTo("Tokyo");
        assertThat(address.state()).isEqualTo("Kanto");
        assertThat(address.country()).isEqualTo("Japan");
    });

    assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);

    assertThat(customer.contacts()).hasSize(7);
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Yuki");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARTNER);
        assertThat(contact.withdrawalLimit()).isEqualTo(1000);
//        assertThat(contact.emergencyContact()).isFalse();
        assertThat(contact.startDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(contact.endDate()).isNull();
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Aiko");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.CHILD);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Taro");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARENT);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Yumi");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARENT);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Kobayashi");
        assertThat(contact.firstName()).isEqualTo("Hana");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.SIBLING);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Sota");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.SIBLING);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Saito");
        assertThat(contact.firstName()).isEqualTo("Ryo");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.COLLEAGUE);
    });

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + HIROSHI_ID;
    assertThat(customer._links()).satisfies(links -> {
        assertThat(links.self().href()).isEqualTo(idUri);
        assertThat(links.list().href()).isEqualTo(baseUri);
        assertThat(links.add().href()).isEqualTo(baseUri);
        assertThat(links.update().href()).isEqualTo(idUri);
        assertThat(links.remove().href()).isEqualTo(idUri);
    });
}

 @Test
void testGetFullHiroshiByIdAsUser() {
    ResponseEntity<TestCustomer> response = clients.userClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/all/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    var customer = response.getBody();

    assertThat(customer.username()).isEqualTo("hiroshi.tanaka");
    assertThat(customer.lastName()).isEqualTo("Tanaka");
    assertThat(customer.firstName()).isEqualTo("Hiroshi");
    assertThat(customer.email()).isEqualTo("hiroshi.tanaka@example.com");
    assertThat(customer.phoneNumber()).isEqualTo("+81-3-1234-5678");
    assertThat(customer.subscribed()).isTrue();
    assertThat(customer.tierLevel()).isEqualTo(1);
    assertThat(customer.birthDate()).isEqualTo(LocalDate.of(1988, 6, 20));
    assertThat(customer.customerState()).isEqualTo(StatusType.ACTIVE);
    assertThat(customer.gender()).isEqualTo(GenderType.MALE);
    assertThat(customer.maritalStatus()).isEqualTo(MaritalStatusType.MARRIED);

    assertThat(customer.address()).satisfies(address -> {
        assertThat(address.street()).isEqualTo("Shibuya Crossing");
        assertThat(address.houseNumber()).isEqualTo("1-2-3");
        assertThat(address.zipCode()).isEqualTo("150-0001");
        assertThat(address.city()).isEqualTo("Tokyo");
        assertThat(address.state()).isEqualTo("Kanto");
        assertThat(address.country()).isEqualTo("Japan");
    });

    assertThat(customer.interests()).containsExactly(InterestType.TECHNOLOGY_AND_INNOVATION);
    assertThat(customer.contactOptions()).containsExactlyInAnyOrder(ContactOptionsType.EMAIL, ContactOptionsType.PHONE);

    assertThat(customer.contacts()).hasSize(7);
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Yuki");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARTNER);
        assertThat(contact.withdrawalLimit()).isEqualTo(1000);
//        assertThat(contact.emergencyContact()).isFalse();
        assertThat(contact.startDate()).isEqualTo(LocalDate.of(2020, 1, 1));
        assertThat(contact.endDate()).isNull();
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Aiko");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.CHILD);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Taro");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARENT);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Yumi");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.PARENT);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Kobayashi");
        assertThat(contact.firstName()).isEqualTo("Hana");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.SIBLING);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Tanaka");
        assertThat(contact.firstName()).isEqualTo("Sota");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.SIBLING);
    });
    assertThat(customer.contacts()).anySatisfy(contact -> {
        assertThat(contact.lastName()).isEqualTo("Saito");
        assertThat(contact.firstName()).isEqualTo("Ryo");
        assertThat(contact.relationship()).isEqualTo(RelationshipType.COLLEAGUE);
    });

    String baseUri = SCHEMA_HOST + port + CUSTOMER_PATH;
    String idUri = baseUri + "/" + HIROSHI_ID;
    assertThat(customer._links()).satisfies(links -> {
        assertThat(links.self().href()).isEqualTo(idUri);
        assertThat(links.list().href()).isEqualTo(baseUri);
        assertThat(links.add().href()).isEqualTo(baseUri);
        assertThat(links.update().href()).isEqualTo(idUri);
        assertThat(links.remove().href()).isEqualTo(idUri);
    });
}

 @Test
 void testGetFullHiroshiByIdAsSupreme() {
    ResponseEntity<TestCustomer> response = clients.supremeClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/all/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
}
  @Test
  void testGetFullHiroshiByIdAsElite() {
    ResponseEntity<TestCustomer> response = clients.eliteClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/all/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetFullHiroshiByIdAsBasic() {
    ResponseEntity<TestCustomer> response = clients.basicClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/all/" + HIROSHI_ID, TestCustomer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testGetHiroshiByIdNotModified() {
    // Speichern Sie die ursprünglichen Interceptoren
    List<ClientHttpRequestInterceptor> originalInterceptors = new ArrayList<>(clients.adminClient.getRestTemplate().getInterceptors());

    // Fügen Sie einen neuen Interceptor hinzu, der den If-None-Match Header setzt
    clients.adminClient.getRestTemplate().getInterceptors().add((request, body, execution) -> {
      request.getHeaders().set(HEADER_IF_NONE_MATCH, ETAG_VALUE_0);
      return execution.execute(request, body);
    });

    try {
      var response = clients.adminClient.getForEntity(SCHEMA_HOST + port + CUSTOMER_PATH + "/" + HIROSHI_ID, TestCustomer.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_MODIFIED);
    } finally {
      // Stellen Sie die ursprünglichen Interceptoren wieder her
      clients.adminClient.getRestTemplate().setInterceptors(originalInterceptors);
    }
  }
}


