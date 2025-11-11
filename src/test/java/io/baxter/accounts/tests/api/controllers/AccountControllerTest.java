package io.baxter.accounts.tests.api.controllers;

import io.baxter.accounts.api.controllers.AccountController;
import io.baxter.accounts.api.services.AccountService;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

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
    void test(){
        assertThat(accountController).isNotNull();
    }
}
