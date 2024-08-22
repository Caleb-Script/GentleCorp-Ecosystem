
package com.gentle.bank.customer.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;

/**
 * Entity-Klasse, um Benutzerkennungen bestehend aus Benutzername,
 * Passwort und Rollen zu repräsentieren, die in der DB verwaltet
 * werden.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Entity
@Table(name = "login")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@SuppressWarnings("MissingSummary")
public class Login {
    @Id
    @GeneratedValue
    // Oracle: https://in.relation.to/2022/05/12/orm-uuid-mapping
    // @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.CHAR)
    @EqualsAndHashCode.Include
    private UUID id;

    private String username;

    private String password;

    @Transient
    private List<Rolle> rollen;

    @Column(name = "rollen")
    private String rollenStr;

    /**
     * Konvertierungsfunktion, um ein User-Objekt aus der DB in ein User-Objekt für Spring Security zu konvertieren.
     *
     * @return Ein Objekt von [CustomUser] für Spring Security
     */
    UserDetails toUserDetails() {
        final List<SimpleGrantedAuthority> authorities = rollen == null || rollen.isEmpty()
            ? emptyList()
            : rollen.stream()
                .map(rolle -> new SimpleGrantedAuthority(Rolle.ROLE_PREFIX + rolle))
                .toList();
        return new CustomUser(username, password, authorities);
    }

    @PrePersist
    private void buildRollenStr() {
        final var rollenStrList = rollen.stream().map(Enum::name).toList();
        rollenStr = String.join(",", rollenStrList);
    }

    @PostLoad
    private void loadRollen() {
        final var rollenArray = rollenStr.split(",");
        rollen = Arrays.stream(rollenArray).map(Rolle::valueOf).toList();
    }
}
