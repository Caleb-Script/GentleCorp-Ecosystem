package com.gentle.bank.customer.util;

import com.gentle.bank.customer.service.exception.VersionInvalidException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.Optional;

import static com.gentle.bank.customer.util.Constants.VERSION_NUMBER_MISSING;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
@Slf4j public class VersionUtils {

    public static int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
        log.trace("getVersion: {}", versionOpt);
        return versionOpt.map(versionStr -> {
            if (isValidVersion(versionStr)) {
                return Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
            } else {
                throw new VersionInvalidException(
                        PRECONDITION_FAILED,
                        STR."Ungueltiges ETag \{versionStr}",
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
        log.debug("länger des versionStrings={} versionString={}",versionStr.length(),versionStr);
        return versionStr.length() >= 3 &&
                versionStr.charAt(0) == '"' &&
                versionStr.charAt(versionStr.length() - 1) == '"';
    }
}
