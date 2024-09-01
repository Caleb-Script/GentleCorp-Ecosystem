package com.gentlecorp.invoice.dev;

import org.springframework.context.annotation.Profile;

@Profile(DevConfig.DEV)
public class DevConfig implements Flyway, LogRequestHeaders, LogPasswordEncoding, LogSignatureAlgorithms, K8s {
  public static final String DEV = "dev";

  DevConfig() {
  }
}
