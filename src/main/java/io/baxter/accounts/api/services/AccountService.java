package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.LoginRequest;
import io.baxter.accounts.api.models.LoginResponse;
import io.baxter.accounts.api.models.RegistrationRequest;
import io.baxter.accounts.api.models.RegistrationResponse;
import io.baxter.accounts.data.models.AccountDataModel;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<LoginResponse> login(LoginRequest loginRequest);
    Mono<RegistrationResponse> register(RegistrationRequest registrationRequest);
}
