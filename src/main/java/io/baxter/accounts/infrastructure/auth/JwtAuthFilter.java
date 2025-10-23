package io.baxter.accounts.infrastructure.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter implements WebFilter {
    private final String secret;

    public JwtAuthFilter(@Value("${jwt.secret}") String secret){ this.secret = secret; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // if no auth header is provided as expected by 'Bearer token' return 401
        List<String> authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String header = authHeaders.getFirst();
        if (!header.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // get token value from bearer
        String token = header.substring(7);

        try {
            // get key from secret
            Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // get claims from token using key
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // subject should be provided username
            String username = claims.getSubject();

            // roles should be provided in token
            List<String> roles = claims.get("roles", List.class);

            // map to simple granted authorities
            List<SimpleGrantedAuthority> authorities = roles != null ?
                    roles.stream().map(SimpleGrantedAuthority::new).toList() :
                    new ArrayList<>();

            // set authorization token - for now all users are ROLE_USER
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // write auth to chain context
            return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
