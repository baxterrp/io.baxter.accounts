package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.AccountModel;
import io.baxter.accounts.api.models.UpdateAccountRequest;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.models.register.RegistrationResponse;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<RegistrationResponse> register(RegistrationRequest registrationRequest);
    Mono<AccountModel> getAccountById(Integer id);
    Mono<AccountModel> updateAccount(UpdateAccountRequest updateAccountRequest, Integer id);
}
