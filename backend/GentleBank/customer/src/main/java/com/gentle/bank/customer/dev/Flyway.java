package com.gentle.bank.customer.dev;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;

interface Flyway {
    /**
     * Bean-Definition, um eine Migrationsstrategie für Flyway im Profile "dev" bereitzustellen, sodass zuerst alle
     * Tabellen, Indexe etc. gelöscht und dann neu aufgebaut werden.
     *
     * @return FlywayMigrationStrategy
     */
    @Bean
    default FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }
}
