package io.baxter.accounts.infrastructure.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AsyncSecurityConfiguration {
    private final ReactiveJwtAuthFilter filter;
    private final String ACCOUNT_CONSUMER_ROLE = "ACCOUNT_CONSUMER_API";

    @Bean
    public SecurityWebFilterChain securityWebChainFilter(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/login/**", "/register/**")
                            .hasAuthority(ACCOUNT_CONSUMER_ROLE)
                        .anyExchange().authenticated())
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
