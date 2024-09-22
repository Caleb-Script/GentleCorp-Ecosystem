package com.gentlecorp.customer;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class TestConfig {

  private final TestRestTemplate restTemplate;

  public TestConfig(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public TestRestTemplate createVisitorClient() {
    TestRestTemplate authenticatedClient = new TestRestTemplate();
    authenticatedClient.getRestTemplate().setInterceptors(
      List.of((request, body, execution) -> {
        request.getHeaders().add("If-None-Match", "\"-1\"");
        request.getHeaders().add("If-Match", "\"0\"");
        return execution.execute(request, body);
      })
    );

    return authenticatedClient;
  }

  public TestRestTemplate createAuthenticatedClient(String username, String password) {
    ResponseEntity<Map> response = restTemplate.postForEntity(
      "/auth/login",
      Map.of("username", username, "password", password),
      Map.class
    );

    assert response.getStatusCode().is2xxSuccessful();
    String token = (String) Objects.requireNonNull(response.getBody()).get("access_token");

    TestRestTemplate authenticatedClient = new TestRestTemplate();
    authenticatedClient.getRestTemplate().setInterceptors(
      List.of((request, body, execution) -> {
        request.getHeaders().add("Authorization", "Bearer " + token);
        request.getHeaders().add("If-None-Match", "\"-1\"");
        request.getHeaders().add("If-Match", "\"0\"");
        return execution.execute(request, body);
      })
    );

    return authenticatedClient;
  }
}
