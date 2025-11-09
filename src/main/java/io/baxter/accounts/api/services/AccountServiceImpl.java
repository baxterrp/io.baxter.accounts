package io.baxter.accounts.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.models.register.RegistrationResponse;
import io.baxter.accounts.data.models.*;
import io.baxter.accounts.data.repository.*;
import io.baxter.accounts.infrastructure.behavior.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
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
                                        foundAccount.getUserId(),
                                        foundAccount.getEmail(),
                                        phone != null ? phone.getId() : foundAccount.getPhoneId(),
                                        address != null ? address.getId() : foundAccount.getAddressId()
                                );

                                return accountRepository.save(updatedAccount);
                            })
                            .flatMap(saved -> getAccountById(saved.getId()));
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
                               UUID.fromString(accountDataModel.getUserId()),
                               accountDataModel.getEmail(),
                               tuple.getT1().orElse(null),
                               tuple.getT2().orElse(null)
                       ));
                    });
    }

    @Override
    public Mono<RegistrationResponse> register(RegistrationRequest registrationRequest) {
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
        return saveAccountWithAddressAndPhone(
                addressRequest, phoneRequest, registrationRequest.getEmail(), registrationRequest.getUserId());
    }

    private Mono<RegistrationResponse> saveAccountWithAddressAndPhone(
            AddressDataModel addressRequest,
            PhoneNumberDataModel phoneRequest,
            String email,
            UUID userId
    ) {
        Mono<Optional<AddressDataModel>> addressMono =
                addressRequest != null
                        ? addressRepository.save(addressRequest).map(Optional::of)
                        : Mono.just(Optional.empty());

        Mono<Optional<PhoneNumberDataModel>> phoneMono =
                phoneRequest != null
                        ? phoneNumberRepository.save(phoneRequest).map(Optional::of)
                        : Mono.just(Optional.empty());

        return Mono.zip(addressMono, phoneMono)
                .flatMap(tuple -> {
                    Optional<AddressDataModel> address = tuple.getT1();
                    Optional<PhoneNumberDataModel> phone = tuple.getT2();

                    AccountDataModel account = new AccountDataModel(
                            null,
                            userId.toString(),
                            email,
                            phone.map(PhoneNumberDataModel::getId).orElse(null),
                            address.map(AddressDataModel::getId).orElse(null)
                    );

                    return accountRepository.save(account);
                })
                .map(saved -> new RegistrationResponse(
                        saved.getId(), UUID.fromString(saved.getUserId()), saved.getEmail()));
    }
}

