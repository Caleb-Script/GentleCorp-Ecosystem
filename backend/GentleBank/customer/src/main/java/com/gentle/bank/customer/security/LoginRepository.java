
package com.gentle.bank.customer.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository, um Benutzerkennungen zu suchen und für den Service bereitzustellen.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@Repository
public interface LoginRepository extends JpaRepository<Login, UUID> {
    /**
     * Zu einem gegebenen Username wird das zugehörige Login-Objekt gesucht.
     *
     * @param username Username des gesuchten Login-Objekts
     * @return Das gesuchte Login-Objekt in einem ggf. leeren Optional
     */
    Optional<Login> findByUsername(String username);

    /**
     * Prüfung, ob es bereits einen User mit gegebenem Benutzernamen gibt.
     *
     * @param username Der zu überprüfende Benutzername.
     * @return true, falls es bereits den Benutzernamen gibt, false sonst.
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    boolean existsByUsername(String username);
}
