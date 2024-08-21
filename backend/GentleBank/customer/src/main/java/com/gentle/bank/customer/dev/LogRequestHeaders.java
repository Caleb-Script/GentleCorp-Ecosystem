package com.gentle.bank.customer.dev;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

interface LogRequestHeaders {
    /**
     * WebFilter zur Protokollierung des Request-Headers.
     *
     * @return CommonsRequestLoggingFilter, der den Request-Header protokolliert.
     */
    @Bean
    default CommonsRequestLoggingFilter logFilter() {
        final var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        return filter;
    }
}
