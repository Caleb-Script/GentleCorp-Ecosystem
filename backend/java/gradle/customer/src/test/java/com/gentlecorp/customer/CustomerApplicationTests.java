package com.gentlecorp.customer;

import com.gentlecorp.customer.controller.CustomerReadController;
import com.gentlecorp.customer.controller.CustomerWriteController;
import com.gentlecorp.customer.model.entity.Customer;
import com.gentlecorp.customer.model.enums.ContactOptionsType;
import com.gentlecorp.customer.model.enums.GenderType;
import com.gentlecorp.customer.model.enums.MaritalStatusType;
import com.gentlecorp.customer.model.test.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApplicationTests {

  private static final String SCHEMA_HOST = "http://localhost:";

  public static final String ADMIN = "admin";
  private static final String USER = "user";
  private static final String SUPREME = "gentlecg99";
  private static final String ELITE = "leroy135";
  private static final String BASIC = "erik";
  private static final String PASSWORD = "p";

  @Autowired
  private CustomerReadController customerReadController;

  @Autowired
  private CustomerWriteController customerWriteController;

  @Test
  void contextLoads() throws Exception {
    assertThat(customerReadController).isNotNull();
    assertThat(customerWriteController).isNotNull();
  }

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TestConfig testConfig;

  @Test
  void greetingShouldReturnDefaultMessage() throws Exception {
    assertThat(this.restTemplate.getForObject(SCHEMA_HOST + port + CUSTOMER_PATH + "/hallo",
      String.class)).contains("Hallo");
  }


  private TestRestTemplate adminClient;
  private TestRestTemplate userClient;
  private TestRestTemplate basicClient;
  private TestRestTemplate eliteClient;
  private TestRestTemplate supremeClient;

  @BeforeEach
  void setUp() {
    adminClient = testConfig.createAuthenticatedClient(ADMIN, PASSWORD);
    userClient = testConfig.createAuthenticatedClient(USER, PASSWORD);
    basicClient = testConfig.createAuthenticatedClient(BASIC, PASSWORD);
    eliteClient = testConfig.createAuthenticatedClient(ELITE, PASSWORD);
    supremeClient = testConfig.createAuthenticatedClient(SUPREME, PASSWORD);
  }
}

