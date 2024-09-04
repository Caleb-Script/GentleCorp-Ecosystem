package com.gentlecorp.customer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class ControllerUtils {
  public String createETag(int version) {
    return String.format("\"%s\"", version);
  }

  public boolean isETagMatching(Optional<String> requestVersion, String currentVersion) {
    return Objects.equals(requestVersion.orElse(null), currentVersion);
  }
}
