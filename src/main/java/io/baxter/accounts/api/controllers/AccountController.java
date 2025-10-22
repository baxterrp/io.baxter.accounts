package io.baxter.accounts.api.controllers;

import io.baxter.accounts.api.models.LoginRequest;
import io.baxter.accounts.api.models.LoginResponse;
import io.baxter.accounts.api.models.RegistrationRequest;
import io.baxter.accounts.api.services.AccountService;
import io.baxter.accounts.data.models.AccountDataModel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public Mono<ResponseEntity<AccountDataModel>> register(@Valid @RequestBody RegistrationRequest request){
        return accountService.register(request)
                .map(account -> ResponseEntity.created(URI.create("/login")).body(account));
    }
}
