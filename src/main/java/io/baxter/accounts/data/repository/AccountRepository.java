package io.baxter.accounts.data.repository;

import io.baxter.accounts.data.models.AccountDataModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountDataModel, Integer>{
    Mono<Boolean> existsByEmail(String email);
    Mono<AccountDataModel> findByUserId(UUID userId);
}