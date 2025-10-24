package io.baxter.accounts.api.controllers;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.login.LoginRequest;
import io.baxter.accounts.api.models.login.LoginResponse;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.models.register.RegistrationResponse;
import io.baxter.accounts.api.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        return accountService.login(request).map(ResponseEntity::ok);
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RegistrationResponse>> register(@Valid @RequestBody RegistrationRequest request){
        return accountService.register(request)
                .map(account -> ResponseEntity.created(URI.create("/login")).body(account));
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<AccountModel>> getAccount(@PathVariable Integer id){
        return accountService.getAccountById(id).map(ResponseEntity::ok);
    }

    @PatchMapping("{id}")
    public Mono<ResponseEntity<AccountModel>> update(@PathVariable Integer id, @RequestBody UpdateAccountRequest updateAccountRequest){
        return Mono.just(ResponseEntity.ok(new AccountModel(
                id,
                "test@gmail.com",
                new PhoneModel(id, "1234567890", "1"),
                new AddressModel(id, "123 Fake St.", "ypsilanti", "MI", "48198", "usa"))));
    }
}
