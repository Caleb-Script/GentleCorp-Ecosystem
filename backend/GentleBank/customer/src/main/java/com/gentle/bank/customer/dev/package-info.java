/**
 * Provides configuration classes and utilities for the development environment of the customer management system.
 * <p>
 * This package contains classes and interfaces that are used exclusively when the application is running in
 * the "dev" Spring profile. It includes configurations for:
 * </p>
 * <ul>
 *   <li>Database migration using Flyway.</li>
 *   <li>Logging request headers and passwords for debugging purposes.</li>
 *   <li>Logging available signature algorithms.</li>
 *   <li>Detecting and logging Kubernetes as the cloud platform.</li>
 * </ul>
 * <p>
 * These configurations are intended to facilitate development, debugging, and testing, providing features
 * such as database schema management, detailed request logging, and security algorithm visibility.
 * </p>
 *
 * @see com.gentle.bank.customer.dev.Flyway
 * @see com.gentle.bank.customer.dev.LogRequestHeaders
 * @see com.gentle.bank.customer.dev.LogPasswordEncoding
 * @see com.gentle.bank.customer.dev.LogSignatureAlgorithms
 * @see com.gentle.bank.customer.dev.K8s
 * @see com.gentle.bank.customer.dev.DevConfig
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 */
package com.gentle.bank.customer.dev;
