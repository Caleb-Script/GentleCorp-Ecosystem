package com.gentlecorp.customer;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.stereotype.Component;

@Component
public class Clients {

  public static final String ADMIN = "admin";
  private static final String USER = "user";
  private static final String SUPREME = "gentlecg99";
  private static final String ELITE = "leroy135";
  private static final String BASIC = "erik";
  private static final String PASSWORD = "p";

  @Autowired
  private TestConfig testConfig;

  public TestRestTemplate adminClient;
  public TestRestTemplate userClient;
  public TestRestTemplate basicClient;
  public TestRestTemplate eliteClient;
  public TestRestTemplate supremeClient;
  public TestRestTemplate visitorClient;

 @PostConstruct
 public void init() {
    adminClient = testConfig.createAuthenticatedClient(ADMIN, PASSWORD);
    userClient = testConfig.createAuthenticatedClient(USER, PASSWORD);
    basicClient = testConfig.createAuthenticatedClient(BASIC, PASSWORD);
    eliteClient = testConfig.createAuthenticatedClient(ELITE, PASSWORD);
    supremeClient = testConfig.createAuthenticatedClient(SUPREME, PASSWORD);
    visitorClient = testConfig.createVisitorClient();
  }
}
