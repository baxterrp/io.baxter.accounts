package io.baxter.accounts.infrastructure.auth;

import io.baxter.accounts.infrastructure.constants.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AsyncSecurityConfiguration {
    private final String userScope = "SCOPE_USER";
    private final String accountsUri = "/api/accounts/**";

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.secret-key}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public SecurityWebFilterChain securityWebChainFilter(ServerHttpSecurity http){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.POST, accountsUri).permitAll()
                        .pathMatchers(HttpMethod.GET, accountsUri).hasAuthority(userScope)
                        .pathMatchers(HttpMethod.PATCH, accountsUri).hasAuthority(userScope)
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
