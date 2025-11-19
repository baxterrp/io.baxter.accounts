package io.baxter.accounts.tests.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.register.RegistrationRequest;
import io.baxter.accounts.api.services.AccountServiceImpl;
import io.baxter.accounts.data.models.*;
import io.baxter.accounts.data.repository.*;
import io.baxter.accounts.infrastructure.behavior.exceptions.ResourceNotFoundException;
import io.baxter.accounts.infrastructure.messaging.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class AccountServiceTest {
    @Mock private AccountRepository mockAccountRepository;
    @Mock private AddressRepository mockAddressRepository;
    @Mock private PhoneNumberRepository mockPhoneNumberRepository;
    @Mock private AccountEventPublisher mockEventPublisher;

    @InjectMocks private AccountServiceImpl accountService;

    final private Integer accountId = 1;
    final private UUID userId = UUID.fromString("c284e1b4-214a-4e36-886b-63d6d4279e6a");

    @Test
    void updateAccountWhenNoAccountFoundReturnsResourceNotFoundException(){
        // Arrange
        Mockito.when(mockAccountRepository.findById(accountId)).thenReturn(Mono.empty());

        // Act
        var response = accountService.updateAccount(getValidUpdateAccountRequest(), accountId);

        // Assert
        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                        error.getMessage().equals("No account found with id 1"))
                .verify();

        Mockito.verify(mockAccountRepository).findById(accountId);
    }

    @Test
    void updateAccountWhenAccountFoundReturnsUpdatedAccount(){
        // Arrange
        var expectedAccount = getAccountModel();
        var expectedPhoneDataModel = new PhoneNumberDataModel(accountId, "1234567890", "1");
        var expectedAddressDataModel = new AddressDataModel(accountId, "123 fk st.", "ypsilanti", "MI", "48198", "USA");
        var expectedAccountDataModel = new AccountDataModel(
                accountId, userId.toString(), "test", "user", "test@test.com", accountId, accountId);

        Mockito.when(mockAccountRepository.findById(accountId)).thenReturn(Mono.just(expectedAccountDataModel));
        Mockito.when(mockPhoneNumberRepository.save(any())).thenReturn(Mono.just(expectedPhoneDataModel));
        Mockito.when(mockAddressRepository.save(any())).thenReturn(Mono.just(expectedAddressDataModel));
        Mockito.when(mockAccountRepository.save(any())).thenReturn(Mono.just(expectedAccountDataModel));

        // update account method ends with a call get getAccountByUserId
        Mockito.when(mockAccountRepository.findByUserId(userId)).thenReturn(Mono.just(expectedAccountDataModel));
        Mockito.when(mockPhoneNumberRepository.findById(accountId)).thenReturn(Mono.just(expectedPhoneDataModel));
        Mockito.when(mockAddressRepository.findById(accountId)).thenReturn(Mono.just(expectedAddressDataModel));

        // Act
        var response = accountService.updateAccount(getValidUpdateAccountRequest(), accountId);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(account ->
                        account.getId().equals(expectedAccount.getId()) &&
                                account.getUserId().equals(expectedAccount.getUserId()) &&
                                account.getEmail().equals(expectedAccount.getEmail()) &&
                                account.getFirstName().equals(expectedAccount.getFirstName()) &&
                                account.getLastName().equals(expectedAccount.getLastName()))
                .verifyComplete();

        Mockito.verify(mockAccountRepository).findById(accountId);
    }

    @Test
    void registerWhenEmailOrUserIdExistsReturnsResourceExistsException(){
        // Arrange
        var registrationRequest = new RegistrationRequest(
                userId, "test", "user", "test@test.com", "123pass##", getPhoneModel(), getAddressModel());

        Mockito.when(mockAccountRepository.existsByEmailOrUserId(eq(registrationRequest.getEmail()), eq(userId.toString())))
                .thenReturn(Mono.just(true));

        // Act
        var response = accountService.register(registrationRequest);

        // Assert
        StepVerifier.create(response)
                .expectErrorMatches(error -> error.getMessage().equals("account already exists with value test@test.com"))
                .verify();
    }

    @Test
    void registerWhenEmailAndUserIdNotExistReturnsRegisteredUserAndPublishesAccountRegisteredEvent(){
        // Arrange
        var expectedAccount = getAccountModel();
        var expectedPhoneDataModel = new PhoneNumberDataModel(accountId, "1234567890", "1");
        var expectedAddressDataModel = new AddressDataModel(accountId, "123 fk st.", "ypsilanti", "MI", "48198", "USA");
        var expectedAccountDataModel = new AccountDataModel(
                accountId, userId.toString(), "test", "user", "test@test.com", accountId, accountId);
        var registrationRequest = new RegistrationRequest(
                userId, "test", "user", "test@test.com", "123pass##", getPhoneModel(), getAddressModel());

        Mockito.when(mockAccountRepository.existsByEmailOrUserId(eq("test@test.com"), eq(userId.toString()))).thenReturn(Mono.just(false));
        Mockito.when(mockPhoneNumberRepository.save(any(PhoneNumberDataModel.class))).thenReturn(Mono.just(expectedPhoneDataModel));
        Mockito.when(mockAddressRepository.save(any(AddressDataModel.class))).thenReturn(Mono.just(expectedAddressDataModel));
        Mockito.when(mockAccountRepository.save(any(AccountDataModel.class))).thenReturn(Mono.just(expectedAccountDataModel));

        doNothing()
                .when(mockEventPublisher)
                .publishAccountRegistered(argThat(registeredEvent -> registeredEvent.getUserId().equals(userId)));

        // Act
        var response = accountService.register(registrationRequest);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(account ->
                        account.getId().equals(expectedAccount.getId()) &&
                        account.getUserId().equals(expectedAccount.getUserId()) &&
                        account.getEmail().equals(expectedAccount.getEmail()))
                .verifyComplete();
    }

    @Test
    void getAccountWhenNoAccountFoundReturnsResouceNotFoundException(){
        // Arrange
        Mockito.when(mockAccountRepository.findByUserId(userId)).thenReturn(Mono.empty());

        // Act
        var response = accountService.getAccountByUserId(userId);

        // Assert
        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof ResourceNotFoundException &&
                                error.getMessage().equals("No account found with id c284e1b4-214a-4e36-886b-63d6d4279e6a"))
                .verify();

        Mockito.verify(mockAccountRepository).findByUserId(userId);
    }

    @Test
    void getAccountWhenAccountFoundReturnsAccount(){
        // Arrange
        var expectedAccount = getAccountModel();
        var expectedPhoneDataModel = new PhoneNumberDataModel(accountId, "1234567890", "1");
        var expectedAddressDataModel = new AddressDataModel(accountId, "123 fk st.", "ypsilanti", "MI", "48198", "USA");
        var expectedAccountDataModel = new AccountDataModel(
                accountId, userId.toString(), "test", "user", "test@test.com", accountId, accountId);

        Mockito.when(mockAccountRepository.findByUserId(userId)).thenReturn(Mono.just(expectedAccountDataModel));
        Mockito.when(mockPhoneNumberRepository.findById(accountId)).thenReturn(Mono.just(expectedPhoneDataModel));
        Mockito.when(mockAddressRepository.findById(accountId)).thenReturn(Mono.just(expectedAddressDataModel));

        // Act
        var response = accountService.getAccountByUserId(userId);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(account ->
                        account.getId().equals(expectedAccount.getId()) &&
                        account.getUserId().equals(expectedAccount.getUserId()) &&
                        account.getEmail().equals(expectedAccount.getEmail()) &&
                        account.getFirstName().equals(expectedAccount.getFirstName()) &&
                        account.getLastName().equals(expectedAccount.getLastName()))
                .verifyComplete();

        Mockito.verify(mockAccountRepository).findByUserId(userId);
        Mockito.verify(mockPhoneNumberRepository).findById(accountId);
        Mockito.verify(mockAddressRepository).findById(accountId);
    }

    private UpdateAccountRequest getValidUpdateAccountRequest(){
        return new UpdateAccountRequest("test", "user", getPhoneModel(), getAddressModel());
    }

    private AccountModel getAccountModel(){
        return new AccountModel(accountId, userId, "test@test.com", "test", "user", getPhoneModel(), getAddressModel());
    }

    private PhoneModel getPhoneModel(){
        return new PhoneModel(1, "1234567890", "1");
    }

    private AddressModel getAddressModel(){
        return new AddressModel(1, "123 fk st.", "ypsilanti", "MI", "48198", "USA");
    }
}
