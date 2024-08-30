package com.gentlecorp.account.exception;

import lombok.Getter;

/**
 * Custom runtime exception thrown when an entity or guest cannot be found based on the provided key or search criteria.
 * <p>
 * This exception is used to signal that no entity was found for the given search parameters or key.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public final class IllegalArgumentException extends RuntimeException {
  /**
   * The key or identifier that was not found.
   * <p>
   * This field is used when a specific key or identifier is not found in the system.
   * </p>
   */
    private final String key;

  /**
   * Constructs a new {@code EntityNotFoundException} with a message indicating an invalid key.
   *
   * @param key The key that could not be found.
   */
    public IllegalArgumentException(final String key) {
        super(STR."Ungueltiger Schluessel: \{key}");
        this.key = key;
    }
}
