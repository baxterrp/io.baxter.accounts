package io.baxter.accounts.data.repository;

import io.baxter.accounts.data.models.AddressDataModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends ReactiveCrudRepository<AddressDataModel, Integer> {
}
