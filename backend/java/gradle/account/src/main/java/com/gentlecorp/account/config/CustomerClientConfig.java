package com.gentlecorp.account.config;

import com.gentlecorp.account.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings("java:S1075")
sealed interface CustomerClientConfig permits ApplicationConfig {
    Logger LOGGER = LoggerFactory.getLogger(CustomerClientConfig.class);

    @Bean
    @SuppressWarnings("CallToSystemGetenv")
    default UriComponentsBuilder uriComponentsBuilder() {
        final var customerDefaultPort = 8080;

        // Umgebungsvariable in Kubernetes, sonst: null
        final var customerSchemaEnv = System.getenv("CUSTOMER_SERVICE_SCHEMA");
        final var customerHostEnv = System.getenv("CUSTOMER_SERVICE_HOST");
        final var customerPortEnv = System.getenv("CUSTOMER_SERVICE_PORT");

        // TODO URI bei Docker Compose
        final var schema = customerSchemaEnv == null ? "http" : customerSchemaEnv;
        final var host = customerHostEnv == null ? "localhost" : customerHostEnv;
        final int port = customerPortEnv == null ? customerDefaultPort : Integer.parseInt(customerPortEnv);

        LOGGER.debug("customer: host={}, port={}", host, port);
        return UriComponentsBuilder.newInstance()
            .scheme(schema)
            .host(host)
            .port(port);
    }

    @Bean
    default CustomerRepository customerRepository(
        final UriComponentsBuilder uriComponentsBuilder,
        final RestClient.Builder restClientBuilder,
        final RestClientSsl restClientSsl
    ) {
        final var baseUrl = uriComponentsBuilder.build().toUriString();
        LOGGER.info("REST-Client: baseUrl={}", baseUrl);

        final var restClient = restClientBuilder
            .baseUrl(baseUrl)
            //.apply(restClientSsl.fromBundle("microservice"))
            .build();
        final var clientAdapter = RestClientAdapter.create(restClient);
        final var proxyFactory = HttpServiceProxyFactory.builderFor(clientAdapter).build();
        return proxyFactory.createClient(CustomerRepository.class);
    }
}
