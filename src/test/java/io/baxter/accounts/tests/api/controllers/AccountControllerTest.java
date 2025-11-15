package io.baxter.accounts.tests.api.controllers;

import io.baxter.accounts.api.controllers.AccountController;
import io.baxter.accounts.api.models.*;
import io.baxter.accounts.api.models.register.*;
import io.baxter.accounts.api.services.AccountService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ControllerTest(controllers = AccountController.class)
public class AccountControllerTest {

    @Mock
    private AccountService mockAccountService;

    private AccountController accountController;

    final private UUID userId = UUID.fromString("fd9530d7-17dd-49fa-b187-b3d309cbbd9e");
    final private String email = "test@test.com";
    final private Integer id = 1;
    final private String firstName = "robert";
    final private String lastName = "baxter";

    @BeforeEach
    void setup(){
        accountController = new AccountController(mockAccountService);
    }

    @Test
    @DisplayName("register should return a valid account model when a valid registration is given")
    void registerShouldReturnOkWithAccountModel(){
        // Arrange
        final PhoneModel expectedPhone = getPhoneModel();
        final AddressModel expectedAddress = getAddressModel();
        final String password = "Pass123##";
        final RegistrationResponse expectedResponse = new RegistrationResponse(id, userId, email);
        final RegistrationRequest request = new RegistrationRequest(
                userId, firstName, lastName, email, password, expectedPhone, expectedAddress);

        Mockito.when(mockAccountService.register(request)).thenReturn(Mono.just(expectedResponse));

        // Act
        var response = accountController.register(request);

        // Assert
        StepVerifier.create(response)
                .expectNextMatches(actual ->
                        actual.getStatusCode().equals(HttpStatus.CREATED))
                .verifyComplete();

        Mockito.verify(mockAccountService).register(request);
        Mockito.verifyNoMoreInteractions(mockAccountService);
    }

    @Test
    @DisplayName("update should return a valid account model when a valid account is given")
    void updateShouldReturnOkWithAccountModel(){
        // Arrange
        final String email = "test@test.com";
        final PhoneModel expectedPhone = getPhoneModel();
        final AddressModel expectedAddress = getAddressModel();
        final AccountModel expectedAccountModel = new AccountModel(
                id,
                userId,
                email,
                firstName,
                lastName,
                expectedPhone,
                expectedAddress);

        final UpdateAccountRequest request = new UpdateAccountRequest(firstName, lastName, expectedPhone, expectedAddress);

        Mockito.when(mockAccountService.updateAccount(request, id)).thenReturn(Mono.just(expectedAccountModel));

        // Act
        var response = accountController.update(id, request);

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

        Mockito.verify(mockAccountService).updateAccount(request, id);
        Mockito.verifyNoMoreInteractions(mockAccountService);
    }

    @Test
    @DisplayName("getAccountById should return a valid account model when a valid id is given")
    void getAccountByIdShouldReturnOkWithAccountModel(){
        // Arrange
        final PhoneModel expectedPhone = getPhoneModel();
        final AddressModel expectedAddress = getAddressModel();
        final AccountModel expectedAccountModel = new AccountModel(
                id,
                userId,
                firstName,
                lastName,
                email,
                expectedPhone,
                expectedAddress);

        Mockito.when(mockAccountService.getAccountByUserId(userId)).thenReturn(Mono.just(expectedAccountModel));

        // Act
        var response = accountController.getAccount(userId);

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

        Mockito.verify(mockAccountService).getAccountByUserId(userId);
        Mockito.verifyNoMoreInteractions(mockAccountService);
    }

    private AddressModel getAddressModel(){
        return new AddressModel(
                id,
                "123 fk st.",
                "ypsilanti",
                "mi",
                "48198",
                "USA");
    }

    private PhoneModel getPhoneModel(){
        return new PhoneModel(
                id,
                "1234567890",
                id.toString());
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
