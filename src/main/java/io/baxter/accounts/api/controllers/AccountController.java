package io.baxter.accounts.api.controllers;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.models.register.RegistrationResponse;
import io.baxter.accounts.api.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public Mono<ResponseEntity<RegistrationResponse>> register(@Valid @RequestBody RegistrationRequest request){
        return accountService.register(request)
                .map(account -> ResponseEntity.created(URI.create("/" + account.getId())).body(account));
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<AccountModel>> getAccount(@PathVariable UUID id){
        return accountService.getAccountByUserId(id).map(ResponseEntity::ok);
    }

    @PatchMapping("{id}")
    public Mono<ResponseEntity<AccountModel>> update(@PathVariable Integer id, @RequestBody UpdateAccountRequest updateAccountRequest){
        return accountService.updateAccount(updateAccountRequest, id).map(ResponseEntity::ok);
    }
}
