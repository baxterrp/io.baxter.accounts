package io.baxter.accounts.data.repository;

import io.baxter.accounts.data.models.PhoneNumberDataModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneNumberRepository extends ReactiveCrudRepository<PhoneNumberDataModel, Integer> {
}
