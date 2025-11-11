package io.baxter.accounts.tests.api.controllers;

import io.baxter.accounts.api.controllers.AccountController;
import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.services.AccountService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ControllerTest(controllers = AccountController.class)
public class AccountControllerTest {

    @Mock
    private AccountService mockAccountService;

    private AccountController accountController;

    @BeforeEach
    void setup(){
        accountController = new AccountController(mockAccountService);
    }

    @Test
    @DisplayName("getAccountById should return a valid account model when a valid id is given")
    void getAccountByIdShouldReturnOkWithAccountModel(){
        // Arrange
        final int id = 1;
        final String email = "test@test.com";
        final PhoneModel expectedPhone = new PhoneModel(
                id,
                "1234567890",
                "1");
        final AddressModel expectedAddress = new AddressModel(
                id,
                "123 fk st.",
                "ypsilanti",
                "mi",
                "48198",
                "USA");

        final AccountModel expectedAccountModel = new AccountModel(
                id,
                UUID.randomUUID(),
                email,
                expectedPhone,
                expectedAddress);

        Mockito.when(mockAccountService.getAccountById(id)).thenReturn(Mono.just(expectedAccountModel));

        // Act
        var response = accountController.getAccount(id);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(accountModel -> {
                    var body = accountModel.getBody();
                    var status = accountModel.getStatusCode();

                    return status.equals(HttpStatus.OK) &&
                            body != null &&
                            comparePhones(expectedPhone, body.getPhone()) &&
                            compareAddress(expectedAddress, body.getAddress());
                })
                .verifyComplete();

        Mockito.verify(mockAccountService).getAccountById(id);
        Mockito.verifyNoMoreInteractions(mockAccountService);
    }

    private static boolean compareAddress(AddressModel expectedAddress, AddressModel actualAddress){
        return expectedAddress != null &&
                actualAddress != null &&
                expectedAddress.getId().equals(actualAddress.getId()) &&
                expectedAddress.getStreet().equals(actualAddress.getStreet()) &&
                expectedAddress.getCity().equals(actualAddress.getCity()) &&
                expectedAddress.getState().equals(actualAddress.getState()) &&
                expectedAddress.getZip().equals(actualAddress.getZip()) &&
                expectedAddress.getCountry().equals(actualAddress.getCountry());
    }

    private static boolean comparePhones(PhoneModel expectedPhone, PhoneModel actualPhone){
        return expectedPhone != null &&
                actualPhone != null &&
                expectedPhone.getId().equals(actualPhone.getId()) &&
                expectedPhone.getNumber().equals(actualPhone.getNumber()) &&
                expectedPhone.getCountrycode().equals(actualPhone.getCountrycode());
    }
}
