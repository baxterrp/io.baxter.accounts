package io.baxter.accounts.data.repository;

import io.baxter.accounts.data.models.AccountDataModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<AccountDataModel, Integer> {
}
