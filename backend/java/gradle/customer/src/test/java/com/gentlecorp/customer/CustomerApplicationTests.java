package com.gentlecorp.customer;

import com.gentlecorp.customer.controller.CustomerReadController;
import com.gentlecorp.customer.controller.CustomerWriteController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.gentlecorp.customer.util.Constants.CUSTOMER_PATH;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerApplicationTests {

  private static final String SCHEMA_HOST = "http://localhost:";

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

//  @Autowired
//  private TestConfig testConfig;

  @Test
  void greetingShouldReturnDefaultMessage() throws Exception {
    assertThat(this.restTemplate.getForObject(SCHEMA_HOST + port + CUSTOMER_PATH + "/hallo",
      String.class)).contains("Hallo");
  }


//  public TestRestTemplate adminClient;
//  public TestRestTemplate userClient;
//  public TestRestTemplate basicClient;
//  public TestRestTemplate eliteClient;
//  public TestRestTemplate supremeClient;
//
//  @BeforeEach
//  void setUp() {
//    adminClient = testConfig.createAuthenticatedClient(ADMIN, PASSWORD);
//    userClient = testConfig.createAuthenticatedClient(USER, PASSWORD);
//    basicClient = testConfig.createAuthenticatedClient(BASIC, PASSWORD);
//    eliteClient = testConfig.createAuthenticatedClient(ELITE, PASSWORD);
//    supremeClient = testConfig.createAuthenticatedClient(SUPREME, PASSWORD);
//  }
}

