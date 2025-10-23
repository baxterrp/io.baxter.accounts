package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.data.models.*;
import io.baxter.accounts.data.repository.*;
import io.baxter.accounts.infrastructure.constants.Roles;
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

    @Override
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return authServiceHttpClient.login(new AuthLoginRequest(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @Override
    public Mono<RegistrationResponse> register(RegistrationRequest registrationRequest) {

        // build DTO's
        AuthServiceRegistrationModel authRequest = new AuthServiceRegistrationModel(
                registrationRequest.getEmail(),
                registrationRequest.getPassword(),
                List.of(Roles.ROLE_USER)
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

    private Mono<RegistrationResponse> saveAccountWithAddressAndPhone(
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
                            savedAddress.getId(),
                            savedPhone.getId()
                    );

                    return accountRepository.save(account);
                })
                .flatMap(this::accountToRegistrationResponse);
    }

    private Mono<RegistrationResponse> accountToRegistrationResponse(AccountDataModel accountDataModel){
        return Mono.just(new RegistrationResponse(accountDataModel.getId(), accountDataModel.getEmail()));
    }
}

