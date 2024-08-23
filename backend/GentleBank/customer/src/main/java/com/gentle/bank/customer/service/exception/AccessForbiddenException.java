
package com.gentle.bank.customer.service.exception;

import com.gentle.bank.customer.security.Rolle;
import lombok.Getter;

import java.util.Collection;

/**
 * Exception, falls der Zugriff wegen fehlender Rollen nicht erlaubt ist.
 *
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class AccessForbiddenException extends RuntimeException {
    /**
     * Vorhandene Rollen.
     */
    private final String role;

    @SuppressWarnings("ParameterHidesMemberVariable")
    public AccessForbiddenException(final String role) {
        super("Unzureichende Rolle als : " + role);
        this.role = role;
    }
}
