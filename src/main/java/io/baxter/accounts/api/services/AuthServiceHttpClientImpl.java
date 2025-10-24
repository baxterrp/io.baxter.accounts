package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.login.AuthLoginRequest;
import io.baxter.accounts.api.models.login.LoginResponse;
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
        String loginUri = "/api/auth/login";

        return authServiceWebClient
                .post()
                .uri(loginUri)
                .bodyValue(loginRequest)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse -> Mono.error(new InvalidLoginException()))
                .bodyToMono(LoginResponse.class);
    }

    @Override
    public Mono<Void> register(AuthServiceRegistrationModel request) {
        String registerUri = "/api/auth/register";

        return authServiceWebClient
                .post()
                .uri(registerUri)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("Auth API error: " + body)))
                )
                .bodyToMono(Void.class);
    }
}
