
package com.gentle.bank.customer.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static java.util.Locale.GERMAN;

/**
 * Service-Klasse, um Benutzerkennungen zu suchen und neu anzulegen.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S5852")
public class CustomUserDetailsService implements UserDetailsService {
    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE = Pattern.compile(".*[a-z].*");
    private static final Pattern NUMBERS = Pattern.compile(".*\\d.*");
    @SuppressWarnings("RegExpRedundantEscape")
    private static final Pattern SYMBOLS = Pattern.compile(".*[!-/:-@\\[-`{-\\~].*");

    private final LoginRepository repo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Zu einem gegebenen Username wird der zugehörige User gesucht.
     *
     * @param username Username des gesuchten Users
     * @return Der gesuchte User oder null
     */
    @Override
    public UserDetails loadUserByUsername(final String username) {
        log.debug("loadUserByUsername: {}", username);
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        final var login = repo
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username = " + username));
        final var userDetails = login.toUserDetails();
        log.debug("loadUserByUsername: {}", userDetails);
        return userDetails;
    }

    /**
     * Ein Login-Objekt bauen und in der DB abspeichern.
     *
     * @param user Der neu anzulegende User
     * @return Ein neu gebautes Login-Objekt
     * @throws PasswordInvalidException falls das Passwort ungültig ist
     * @throws UsernameExistsException falls der Benutzername bereits existiert
     */
    public Login save(final UserDetails user) {
        log.debug("save: {}", user);
        final var login = userDetailsToLogin(user);
        repo.save(login);
        return login;
    }

    private Login userDetailsToLogin(final UserDetails user) {
        log.debug("userDetailsToLogin: {}", user);

        final var password = user.getPassword();
        if (!checkPassword(password)) {
            throw new PasswordInvalidException(password);
        }

        final var username = user.getUsername();
        final var isUsernameExisting = repo.existsByUsername(username);
        if (isUsernameExisting) {
            throw new UsernameExistsException(username);
        }

        // Die Account-Informationen des Kunden transformieren: in Account-Informationen fuer die Security-Komponente
        final var login = new Login();
        login.setUsername(username.toLowerCase(GERMAN));

        final var encodedPassword = passwordEncoder.encode(password);
        login.setPassword(encodedPassword);

        final var rollen = user.getAuthorities()
            .stream()
            .map(grantedAuthority -> {
                final var rolleStr = grantedAuthority
                    .getAuthority()
                    .substring(Rolle.ROLE_PREFIX.length());
                return Rolle.valueOf(rolleStr);
            })
            .toList();
        login.setRollen(rollen);

        log.trace("userDetailsToLogin: login = {}", login);
        return login;
    }

    // https://github.com/making/yavi/blob/develop/src/main/java/am/ik/yavi/constraint/password/PasswordPolicy.java
    @SuppressWarnings("ReturnCount")
    private boolean checkPassword(final CharSequence password) {
        if (password.length() < MIN_LENGTH) {
            return false;
        }
        if (!UPPERCASE.matcher(password).matches()) {
            return false;
        }
        if (!LOWERCASE.matcher(password).matches()) {
            return false;
        }
        if (!NUMBERS.matcher(password).matches()) {
            return false;
        }
        return SYMBOLS.matcher(password).matches();
    }
}
