package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.login.AuthLoginRequest;
import io.baxter.accounts.api.models.login.LoginResponse;
import io.baxter.accounts.infrastructure.http.models.AuthServiceRegistrationModel;
import reactor.core.publisher.Mono;

public interface AuthServiceHttpClient {
    Mono<LoginResponse> login(AuthLoginRequest loginRequest);
    Mono<Void> register(AuthServiceRegistrationModel request);
}
