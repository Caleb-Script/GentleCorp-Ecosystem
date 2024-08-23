package com.gentle.bank.customer.dev;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;

/**
 * Interface that defines a Flyway migration strategy for the development profile.
 * <p>
 * This interface provides a default method that configures a migration strategy for Flyway,
 * which first cleans the database by removing all tables, indexes, etc., and then
 * performs a fresh migration. This strategy is particularly useful in a development
 * environment where frequent resets of the database schema are required.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Defines a migration strategy that first cleans the database before applying migrations.</li>
 *   <li>Ensures that the database schema is always in a known state during development.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * This interface is intended to be implemented in the "dev" profile, where it is common to reset
 * the database schema frequently to reflect changes in the development process.
 * The `cleanMigrateStrategy` method is defined as a default method, providing
 * a ready-to-use implementation.
 * </p>
 *
 * @author Caleb Gyamfi
 * @version 1.0
 * @since 23.08.2024
 */
interface Flyway {

  /**
   * Bean definition that provides a migration strategy for Flyway in the "dev" profile.
   * This strategy cleans the database by deleting all tables, indexes, etc., and then
   * rebuilds the schema by applying migrations.
   *
   * <h2>Returns:</h2>
   * <p>A {@link FlywayMigrationStrategy} that defines the clean-and-migrate approach.</p>
   *
   * @return a FlywayMigrationStrategy that first cleans and then migrates the database schema.
   */
  @Bean
  default FlywayMigrationStrategy cleanMigrateStrategy() {
    return flyway -> {
      flyway.clean();
      flyway.migrate();
    };
  }
}
