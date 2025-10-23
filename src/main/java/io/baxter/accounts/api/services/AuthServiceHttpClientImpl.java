package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.AuthLoginRequest;
import io.baxter.accounts.api.models.LoginResponse;
import io.baxter.accounts.infrastructure.behavior.exceptions.InvalidLoginException;
import io.baxter.accounts.infrastructure.http.models.AuthServiceRegistrationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceHttpClientImpl implements AuthServiceHttpClient{
    private final WebClient authServiceWebClient;

    @Override
    public Mono<LoginResponse> login(AuthLoginRequest loginRequest) {
        return authServiceWebClient
                .post()
                .uri("/api/auth/login")
                .bodyValue(loginRequest)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> Mono.error(new InvalidLoginException()))
                .bodyToMono(LoginResponse.class);
    }

    @Override
    public Mono<Void> register(AuthServiceRegistrationModel request) {
        return authServiceWebClient
                .post()
                .uri("/api/auth/register")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Auth API error: " + body)))
                )
                .bodyToMono(Void.class);
    }
}
