package com.gentlecorp.invoice.exception;

import lombok.Getter;

/**
 * Exception thrown when a request contains an outdated version number.
 * <p>
 * This exception is used to indicate that the provided version number is no longer supported or valid,
 * and the request cannot be processed with this version. This may be used in versioning schemes for APIs
 * or other version-controlled components of the system.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Getter
public class VersionOutdatedException extends RuntimeException {
  /**
   * The outdated version number that caused the exception.
   */
  private final int version;

  /**
   * Constructs a new {@code VersionOutdatedException} with the specified version number.
   *
   * @param version The outdated version number.
   */
  public VersionOutdatedException(final int version) {
    super("Die Versionsnummer " + version + " ist veraltet.");
    this.version = version;
  }
}
