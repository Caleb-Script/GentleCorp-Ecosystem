package com.gentle.bank.customer.dev;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import static org.springframework.boot.cloud.CloudPlatform.KUBERNETES;

interface K8s {
    /**
     * Protokoll-Ausgabe, wenn Kubernetes erkannt wird.
     *
     * @return Listener für den ApplicationReadyEvent, um Kubernetes zu erkennen.
     */
    @Bean
    @ConditionalOnCloudPlatform(KUBERNETES)
    default ApplicationListener<ApplicationReadyEvent> detectK8s() {
        return event -> LoggerFactory.getLogger(K8s.class).debug("Plattform \"Kubernetes\"");
    }
}
