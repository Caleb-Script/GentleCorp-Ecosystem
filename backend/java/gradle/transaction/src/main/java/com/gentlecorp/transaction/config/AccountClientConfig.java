package com.gentlecorp.transaction.config;

import com.gentlecorp.transaction.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings("java:S1075")
sealed interface AccountClientConfig permits ApplicationConfig {
    Logger LOGGER = LoggerFactory.getLogger(AccountClientConfig.class);

    @Bean
    @SuppressWarnings("CallToSystemGetenv")
    default UriComponentsBuilder uriComponentsBuilder() {
        final var accountDefaultPort = 8081;

        // Umgebungsvariable in Kubernetes, sonst: null
        final var accountSchemaEnv = System.getenv("ACCOUNT_SERVICE_SCHEMA");
        final var accountHostEnv = System.getenv("ACCOUNT_SERVICE_HOST");
        final var accountPortEnv = System.getenv("ACCOUNT_SERVICE_PORT");

        // TODO URI bei Docker Compose
        final var schema = accountSchemaEnv == null ? "http" : accountSchemaEnv;
        final var host = accountHostEnv == null ? "localhost" : accountHostEnv;
        final int port = accountPortEnv == null ? accountDefaultPort : Integer.parseInt(accountPortEnv);

        LOGGER.debug("account: host={}, port={}", host, port);
        return UriComponentsBuilder.newInstance()
            .scheme(schema)
            .host(host)
            .port(port);
    }

    @Bean
    default AccountRepository accountRepository(
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
        return proxyFactory.createClient(AccountRepository.class);
    }
}
