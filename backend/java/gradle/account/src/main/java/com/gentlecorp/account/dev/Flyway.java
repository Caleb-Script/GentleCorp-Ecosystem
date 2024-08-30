package com.gentlecorp.account.dev;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;

interface Flyway {
  @Bean
  default FlywayMigrationStrategy cleanMigrateStrategy() {
    return flyway -> {
      flyway.clean();
      flyway.migrate();
    };
  }
}
