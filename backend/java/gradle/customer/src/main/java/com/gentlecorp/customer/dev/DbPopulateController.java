package com.gentlecorp.customer.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.gentlecorp.customer.dev.DevConfig.DEV;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@Controller
@RequestMapping("/dev")
@RequiredArgsConstructor
@Slf4j
@Profile(DEV)
public class DbPopulateController {
  private final Flyway flyway;

  @PostMapping(value = "db_populate", produces = TEXT_PLAIN_VALUE)
  public ResponseEntity<String> dbPopulate() {
    log.warn("Die DB wird neu geladen");
    flyway.clean();
    flyway.migrate();
    log.warn("Die DB wurde neu geladen");
    return ok("ok");
  }
}
