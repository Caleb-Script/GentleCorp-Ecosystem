package com.gentle.bank.customer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

public interface CustomerClientConfig {

    int TRANSFER_DEFAULT_PORT = 8082;
    int ACCOUNT_DEFAULT_PORT = 8081;
    String GRAPHQL_PATH = "/graphql";
    Logger LOGGER = LoggerFactory.getLogger(CustomerClientConfig.class);


    @Bean
    @Qualifier("transferUriComponentsBuilder")
    default UriComponentsBuilder transferUriComponentsBuilder() {
        final var transferSchemaEnv = System.getenv("TRANSFER_SERVICE_SCHEMA");
        final var transferHostEnv = System.getenv("TRANSFER_SERVICE_HOST");
        final var transferPortEnv = System.getenv("TRANSFER_SERVICE_PORT");

        final var schema = transferSchemaEnv == null ? "http" : transferSchemaEnv;
        final var host = transferHostEnv == null ? "localhost" : transferHostEnv;
        final int port = transferPortEnv == null ? TRANSFER_DEFAULT_PORT : Integer.parseInt(transferPortEnv);

        LOGGER.debug("transfer: host={}, port={}", host, port);
        return UriComponentsBuilder.newInstance()
                .scheme(schema)
                .host(host)
                .port(port);
    }

    @Bean
    default HttpGraphQlClient transferGraphQlClient(
            @Qualifier("transferUriComponentsBuilder") final UriComponentsBuilder uriComponentsBuilder,
            final WebClient.Builder webClientBuilder,
            final WebClientSsl ssl
    ) {
        final var baseUrl = uriComponentsBuilder
                .path(GRAPHQL_PATH)
                .build()
                .toUriString();
        LOGGER.info("Transfer GraphQL Client: baseUrl={}", baseUrl);

        final var webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        return HttpGraphQlClient.builder(webClient).build();
    }

    @Bean
    @Qualifier("accountUriComponentsBuilder")
    default UriComponentsBuilder accountUriComponentsBuilder() {
        final var accountSchemaEnv = System.getenv("ACCOUNT_SERVICE_SCHEMA");
        final var accountHostEnv = System.getenv("ACCOUNT_SERVICE_HOST");
        final var accountPortEnv = System.getenv("ACCOUNT_SERVICE_PORT");

        final var schema = accountSchemaEnv == null ? "http" : accountSchemaEnv;
        final var host = accountHostEnv == null ? "localhost" : accountHostEnv;
        final int port = accountPortEnv == null ? ACCOUNT_DEFAULT_PORT : Integer.parseInt(accountPortEnv);

        LOGGER.debug("account: host={}, port={}", host, port);
        return UriComponentsBuilder.newInstance()
                .scheme(schema)
                .host(host)
                .port(port);
    }

    @Bean
    default HttpGraphQlClient accountGraphQlClient(
            @Qualifier("accountUriComponentsBuilder") final UriComponentsBuilder uriComponentsBuilder,
            final WebClient.Builder webClientBuilder,
            final WebClientSsl ssl
    ) {
        final var baseUrl = uriComponentsBuilder
                .path(GRAPHQL_PATH)
                .build()
                .toUriString();
        LOGGER.info("Account GraphQL Client: baseUrl={}", baseUrl);

        final var webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
        return HttpGraphQlClient.builder(webClient).build();
    }

}

