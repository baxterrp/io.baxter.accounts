package io.baxter.accounts.api.models.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLoginRequest {
    String userName;
    String password;
}
