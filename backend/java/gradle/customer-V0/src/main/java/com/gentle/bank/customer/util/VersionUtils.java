package com.gentle.bank.customer.util;

import com.gentle.bank.customer.exception.VersionInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static com.gentle.bank.customer.util.Constants.VERSION_NUMBER_MISSING;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;

/**
 * Utility class for handling version-related operations.
 * <p>
 * The {@code VersionUtils} class provides methods to validate and extract version information from
 * HTTP requests. It ensures that version information provided in request headers meets expected formats.
 * </p>
 *
 * @since 24.08.2024
 * @version 1.0
 * @author <a href="mailto:Caleb_G@outlook.de">Caleb Gyamfi</a>
 */
@Slf4j
public class VersionUtils {

  /**
   * Extracts and validates the version from the optional version string.
   * <p>
   * This method checks if the provided version string is valid (i.e., enclosed in double quotes and of
   * sufficient length). If valid, it parses the version number. If the version string is invalid or missing,
   * appropriate exceptions are thrown.
   * </p>
   *
   * @param versionOpt The optional version string extracted from the request headers.
   * @param request The HTTP request from which the version information was derived.
   * @return The version number as an integer.
   * @throws VersionInvalidException If the version string is invalid or missing.
   */
  public static int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
    log.trace("getVersion: {}", versionOpt);
    return versionOpt.map(versionStr -> {
      if (isValidVersion(versionStr)) {
        return Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
      } else {
        throw new VersionInvalidException(
          PRECONDITION_FAILED,
          STR."Invalid ETag \{versionStr}",
          URI.create(request.getRequestURL().toString())
        );
      }
    }).orElseThrow(() -> new VersionInvalidException(
      PRECONDITION_REQUIRED,
      VERSION_NUMBER_MISSING,
      URI.create(request.getRequestURL().toString())
    ));
  }

  private static boolean isValidVersion(String versionStr) {
    log.debug("length of versionString={} versionString={}", versionStr.length(), versionStr);
    return versionStr.length() >= 3 &&
      versionStr.charAt(0) == '"' &&
      versionStr.charAt(versionStr.length() - 1) == '"';
  }
}
