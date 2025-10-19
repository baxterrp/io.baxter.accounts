package io.baxter.accounts.api.controllers;

import io.baxter.accounts.api.models.LoginRequest;
import io.baxter.accounts.api.models.RegistrationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    @PostMapping("/login")
    public Mono<ResponseEntity> login(@Valid @RequestBody LoginRequest request){
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/register")
    public Mono<ResponseEntity> register(@Valid @RequestBody RegistrationRequest request){
        return Mono.just(ResponseEntity.ok().build());
    }
}
