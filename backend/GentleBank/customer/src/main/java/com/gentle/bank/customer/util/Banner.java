package com.gentle.bank.customer.util;

import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public final class Banner {

  static Figlets figlets = new Figlets();
    private static final String FIGLET = figlets.randomFigletGenerator();
    private static final String SERVICE_HOST = System.getenv("CUSTOMER_SERVICE_HOST");
    private static final String KUBERNETES = SERVICE_HOST == null
        ? "N/A"
        : STR."CUSTOMER_SERVICE_HOST=\{SERVICE_HOST}, CUSTOMER_SERVICE_PORT=\{System.getenv("CUSTOMER_SERVICE_PORT")}";

    /**
     * Banner für den Server-Start.
     */
    public static final String TEXT = STR."""

        \{FIGLET}
        (C) Caleb Gyamfi, Gentle Bank
        Version             2024.08.24
        Spring Boot         \{SpringBootVersion.getVersion()}
        Spring Security     \{SpringSecurityCoreVersion.getVersion()}
        Spring Framework    \{SpringVersion.getVersion()}
        Hibernate           \{org.hibernate.Version.getVersionString()}
        Java                \{Runtime.version()} - \{System.getProperty("java.vendor")}
        Betriebssystem      \{System.getProperty("os.name")}
        Rechnername         \{getLocalhost().getHostName()}
        IP-Adresse          \{getLocalhost().getHostAddress()}
        Heap: Size          \{Runtime.getRuntime().totalMemory() / (1024L * 1024L)} MiB
        Heap: Free          \{Runtime.getRuntime().freeMemory() / (1024L * 1024L)} MiB
        Kubernetes          \{KUBERNETES}
        Username            \{System.getProperty("user.name")}
        JVM Locale          \{Locale.getDefault().toString()}
        """;

    private Banner() {
    }

    private static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
