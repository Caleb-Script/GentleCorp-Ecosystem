package com.gentle.bank.customer.dev;

import org.springframework.context.annotation.Profile;

@Profile(DevConfig.DEV)
public class DevConfig implements Flyway, LogRequestHeaders, LogPasswordEncoding, LogSignatureAlgorithms, K8s {
    /**
     * Konstante für das Spring-Profile "dev".
     */
    public static final String DEV = "dev";

    DevConfig() {
    }
}
