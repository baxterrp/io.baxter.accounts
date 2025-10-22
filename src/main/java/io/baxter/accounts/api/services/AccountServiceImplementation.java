package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.data.models.AccountDataModel;
import io.baxter.accounts.data.models.AddressDataModel;
import io.baxter.accounts.data.models.PhoneNumberDataModel;
import io.baxter.accounts.data.repository.AccountRepository;
import io.baxter.accounts.data.repository.AddressRepository;
import io.baxter.accounts.data.repository.PhoneNumberRepository;
import io.baxter.accounts.infrastructure.http.models.AuthServiceRegistrationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImplementation implements AccountService{

    private final AuthServiceHttpClient authServiceHttpClient;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    private final String ROLE_USER = "ROLE_USER";

    @Override
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public Mono<AccountDataModel> register(RegistrationRequest registrationRequest) {

        // build DTO's
        AuthServiceRegistrationModel authRequest = new AuthServiceRegistrationModel(
                registrationRequest.getEmail(),
                registrationRequest.getPassword(),
                List.of(ROLE_USER)
        );

        Address address = registrationRequest.getAddress();
        AddressDataModel addressRequest = address != null ? new AddressDataModel(
                null,
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZip(),
                address.getCountry()
        ) : null;

        Phone phone = registrationRequest.getPhone();
        PhoneNumberDataModel phoneRequest = phone != null ? new PhoneNumberDataModel(
                null,
                phone.getNumber(),
                phone.getCountrycode()
        ) : null;

        // register with auth service and then persist account data
        return authServiceHttpClient.register(authRequest)
                .then(Mono.defer(() -> saveAccountWithAddressAndPhone(addressRequest, phoneRequest, registrationRequest.getEmail())));
    }

    private Mono<AccountDataModel> saveAccountWithAddressAndPhone(
            AddressDataModel addressRequest,
            PhoneNumberDataModel phoneRequest,
            String email
    ) {
        // kick off save phone and address in parallel
        Mono<AddressDataModel> addressMono = addressRepository.save(addressRequest);
        Mono<PhoneNumberDataModel> phoneMono = phoneNumberRepository.save(phoneRequest);

        // "zip" the responses into a tuple and map to access id's
        return Mono.zip(addressMono, phoneMono)
                .flatMap(tuple -> {
                    AddressDataModel savedAddress = tuple.getT1();
                    PhoneNumberDataModel savedPhone = tuple.getT2();

                    // save account with address and phone ids if applicable
                    AccountDataModel account = new AccountDataModel(
                            null,
                            email,
                            savedAddress != null ? savedAddress.getId() : null,
                            savedPhone != null ? savedPhone.getId() : null
                    );

                    return accountRepository.save(account);
                });
    }
}
