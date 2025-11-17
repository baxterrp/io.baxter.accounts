package io.baxter.accounts.tests.api.services;

import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.services.AccountServiceImpl;
import io.baxter.accounts.data.repository.*;
import io.baxter.accounts.infrastructure.behavior.exceptions.ResourceNotFoundException;
import io.baxter.accounts.infrastructure.messaging.AccountEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.OutputCaptureExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@ExtendWith(OutputCaptureExtension.class)
public class AccountServiceTest {
    @Mock private AccountRepository mockAccountRepository;
    @Mock private AddressRepository mockAddressRepository;
    @Mock private PhoneNumberRepository mockPhoneNumberRepository;
    @Mock private AccountEventPublisher mockEventPublisher;

    @InjectMocks private AccountServiceImpl accountService;

    final private Integer accountId = 1;

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
                        error.getMessage().equals("No account found with id 1"));

        Mockito.verify(mockAccountRepository).findById(accountId);
    }

    private UpdateAccountRequest getValidUpdateAccountRequest(){
        return new UpdateAccountRequest(
                "test",
                "user",
                new PhoneModel(1, "1234567890", "1"),
                new AddressModel(1, "123 fk st.", "ypsilanti", "MI", "48198", "USA"));
    }
}
