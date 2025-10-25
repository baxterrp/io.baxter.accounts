package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.login.AuthLoginRequest;
import io.baxter.accounts.api.models.login.LoginRequest;
import io.baxter.accounts.api.models.login.LoginResponse;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.models.register.RegistrationResponse;
import io.baxter.accounts.data.models.*;
import io.baxter.accounts.data.repository.*;
import io.baxter.accounts.infrastructure.behavior.exceptions.InvalidLoginException;
import io.baxter.accounts.infrastructure.behavior.exceptions.ResourceNotFoundException;
import io.baxter.accounts.infrastructure.constants.Roles;
import io.baxter.accounts.infrastructure.http.models.AuthServiceRegistrationModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImplementation implements AccountService{

    private final AuthServiceHttpClient authServiceHttpClient;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final PhoneNumberRepository phoneNumberRepository;

    @Override
    public Mono<AccountModel> updateAccount(UpdateAccountRequest updateAccountRequest, Integer id) {

        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("account", id.toString())))
                .flatMap(foundAccount -> {
                    Mono<Optional<PhoneNumberDataModel>> phoneUpdate = Mono.justOrEmpty(updateAccountRequest.getPhone())
                            .flatMap(phone -> {
                                if (phone.getId() != null) {
                                    PhoneNumberDataModel phoneDataModel = new PhoneNumberDataModel(
                                            phone.getId(),
                                            phone.getNumber(),
                                            phone.getCountrycode()
                                    );

                                    return phoneNumberRepository.save(phoneDataModel);
                                }
                                return Mono.empty();
                            })
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty());

                    Mono<Optional<AddressDataModel>> addressUpdate = Mono.justOrEmpty(updateAccountRequest.getAddress())
                            .flatMap(address -> {
                                if (address.getId() != null) {
                                    AddressDataModel addressDataModel = new AddressDataModel(
                                            address.getId(),
                                            address.getStreet(),
                                            address.getCity(),
                                            address.getState(),
                                            address.getZip(),
                                            address.getCountry()
                                    );

                                    return addressRepository.save(addressDataModel);
                                }

                                return Mono.empty();
                            })
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty());

                    return Mono.zip(phoneUpdate, addressUpdate)
                            .flatMap(tuple -> {
                                PhoneNumberDataModel phone = tuple.getT1().orElse(null);
                                AddressDataModel address = tuple.getT2().orElse(null);

                                AccountDataModel updatedAccount = new AccountDataModel(
                                        id,
                                        foundAccount.getEmail(),
                                        phone != null ? phone.getId() : foundAccount.getPhoneId(),
                                        address != null ? address.getId() : foundAccount.getAddressId()
                                );

                                return accountRepository.save(updatedAccount);
                            })
                            .flatMap(saved -> getAccountById(id));
                });
    }

    @Override
    public Mono<AccountModel> getAccountById(Integer id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("account", id.toString())))
                .flatMap(accountDataModel -> {
                    Mono<Optional<PhoneModel>> phone = Mono.justOrEmpty(accountDataModel.getPhoneId())
                            .flatMap(phoneNumberRepository::findById)
                            .map(phoneDataModel -> new PhoneModel(
                                    phoneDataModel.getId(),
                                    phoneDataModel.getNumber(),
                                    phoneDataModel.getCountrycode()))
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty());

                    Mono<Optional<AddressModel>> address = Mono.justOrEmpty(accountDataModel.getAddressId())
                            .flatMap(addressRepository::findById)
                            .map(addressDataModel -> new AddressModel(
                                    addressDataModel.getId(),
                                    addressDataModel.getStreet(),
                                    addressDataModel.getCity(),
                                    addressDataModel.getState(),
                                    addressDataModel.getZip(),
                                    addressDataModel.getCountry()))
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty());

                    return Mono.zip(phone, address).map(tuple ->
                            new AccountModel(
                               accountDataModel.getId(),
                               accountDataModel.getEmail(),
                               tuple.getT1().orElse(null),
                               tuple.getT2().orElse(null)
                       ));
                    });
    }

    @Override
    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return accountRepository.findByEmail(loginRequest.getEmail())
            .switchIfEmpty(Mono.error(InvalidLoginException::new))
            .flatMap(account -> authServiceHttpClient.login(new AuthLoginRequest(account.getEmail(), loginRequest.getPassword())));
    }

    @Override
    public Mono<RegistrationResponse> register(RegistrationRequest registrationRequest) {
        // build DTO's
        AuthServiceRegistrationModel authRequest = new AuthServiceRegistrationModel(
                registrationRequest.getEmail(),
                registrationRequest.getPassword(),
                List.of(Roles.ROLE_USER)
        );

        AddressModel address = registrationRequest.getAddress();
        AddressDataModel addressRequest = address != null ? new AddressDataModel(
                null,
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZip(),
                address.getCountry()
        ) : null;

        PhoneModel phone = registrationRequest.getPhone();
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
                .map(account -> new RegistrationResponse(account.getId(), account.getEmail()));
    }
}

