package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.LoginRequest;
import io.baxter.accounts.api.models.LoginResponse;
import io.baxter.accounts.infrastructure.http.models.AuthServiceRegistrationModel;
import reactor.core.publisher.Mono;

public interface AuthServiceHttpClient {
    Mono<LoginResponse> login(LoginRequest loginRequest);
    Mono<Void> register(AuthServiceRegistrationModel request);
}
