package com.gentle.bank.customer.util;

import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

/**
 * Utility class to generate and provide a banner with system and application information.
 * <p>
 * This class creates a banner that is displayed at server startup. The banner includes
 * various details about the application's environment, including versions of Spring Boot,
 * Spring Security, Spring Framework, Hibernate, Java, and other system properties.
 * </p>
 * <p>
 * The banner includes the following details:
 * <ul>
 *   <li>Figlet Art</li>
 *   <li>Version</li>
 *   <li>Spring Boot Version</li>
 *   <li>Spring Security Version</li>
 *   <li>Spring Framework Version</li>
 *   <li>Hibernate Version</li>
 *   <li>Java Version and Vendor</li>
 *   <li>Operating System</li>
 *   <li>Hostname and IP Address</li>
 *   <li>Heap Size and Free Memory</li>
 *   <li>Kubernetes Environment Variables</li>
 *   <li>Username</li>
 *   <li>JVM Locale</li>
 * </ul>
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
public final class Banner {

  private static final Figlets figlets = new Figlets();
  private static final String FIGLET = figlets.randomFigletGenerator();
  private static final String SERVICE_HOST = System.getenv("CUSTOMER_SERVICE_HOST");
  private static final String KUBERNETES = SERVICE_HOST == null
    ? "N/A"
    : String.format("CUSTOMER_SERVICE_HOST=%s, CUSTOMER_SERVICE_PORT=%s", SERVICE_HOST, System.getenv("CUSTOMER_SERVICE_PORT"));

  /**
   * The banner text that includes various system and application details.
   * <p>
   * The banner displays information such as the version of the application, Spring versions,
   * Hibernate version, Java version, operating system details, hostname, IP address, memory usage,
   * Kubernetes environment variables, username, and JVM locale.
   * </p>
   */
  public static final String TEXT = String.format("""
        %s
        (C) Caleb Gyamfi, Gentle Bank
        Version             2024.08.24
        Spring Boot         %s
        Spring Security     %s
        Spring Framework    %s
        Hibernate           %s
        Java                %s - %s
        Betriebssystem      %s
        Rechnername         %s
        IP-Adresse          %s
        Heap: Size          %d MiB
        Heap: Free          %d MiB
        Kubernetes          %s
        Username            %s
        JVM Locale          %s
        """,
    FIGLET,
    SpringBootVersion.getVersion(),
    SpringSecurityCoreVersion.getVersion(),
    SpringVersion.getVersion(),
    org.hibernate.Version.getVersionString(),
    Runtime.version(),
    System.getProperty("java.vendor"),
    System.getProperty("os.name"),
    getLocalhost().getHostName(),
    getLocalhost().getHostAddress(),
    Runtime.getRuntime().totalMemory() / (1024L * 1024L),
    Runtime.getRuntime().freeMemory() / (1024L * 1024L),
    KUBERNETES,
    System.getProperty("user.name"),
    Locale.getDefault().toString()
  );

  private Banner() {
    // Private constructor to prevent instantiation
  }

  /**
   * Retrieves the local host's InetAddress.
   * <p>
   * This method is used to obtain the hostname and IP address of the local machine.
   * </p>
   *
   * @return the local host's InetAddress
   * @throws IllegalStateException if the local host cannot be determined
   */
  private static InetAddress getLocalhost() {
    try {
      return InetAddress.getLocalHost();
    } catch (final UnknownHostException ex) {
      throw new IllegalStateException(ex);
    }
  }
}
