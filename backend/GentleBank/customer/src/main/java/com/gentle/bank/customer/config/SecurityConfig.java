package com.gentle.bank.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


interface SecurityConfig {
    int SALT_LENGTH = 32; // default: 16
    int HASH_LENGTH = 64; // default: 32
    int PARALLELISM = 1; // default: 1 (Bouncy Castle kann keine Parallelitaet)
    int NUMBER_OF_BITS = 14;
    int MEMORY_CONSUMPTION_KBYTES = 1 << NUMBER_OF_BITS; // default: 2^14 KByte = 16 MiB  ("Memory Cost Parameter")
    int ITERATIONS = 3; // default: 3

    /**
     * Bean-Definition, um den Zugriffsschutz an der REST-Schnittstelle zu konfigurieren,
     * d.h. vor Anwendung von @PreAuthorize.
     *
     * @param http Injiziertes Objekt von HttpSecurity als Ausgangspunkt für die Konfiguration.
     * @return Objekt von SecurityFilterChain
     * @throws Exception Wegen HttpSecurity.authorizeHttpRequests()
     */
    @Bean
    default SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(authorize -> {
                authorize.anyRequest().permitAll();
            })
            .httpBasic(withDefaults())
            // Spring Security erzeugt keine HttpSession und verwendet keine fuer SecurityContext
            .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
            .build();
    }

    /**
     * Bean-Definition, um den Verschlüsselungsalgorithmus für Passwörter bereitzustellen.
     * Es wird Argon2id statt bcrypt (Default-Algorithmus von Spring Security) verwendet.
     *
     * @return Objekt für die Verschlüsselung von Passwörtern.
     */
    @Bean
    default PasswordEncoder passwordEncoder() {
        final var idForEncode = "argon2id";
        final Map<String, PasswordEncoder> encoders = Map.of(
            idForEncode,
            new Argon2PasswordEncoder(
                SALT_LENGTH,
                HASH_LENGTH,
                PARALLELISM,
                MEMORY_CONSUMPTION_KBYTES,
                ITERATIONS
            )
        );
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}
