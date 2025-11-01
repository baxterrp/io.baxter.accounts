package io.baxter.accounts.api.models.register;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationResponse {
    Integer id;
    Integer userId;
    String email;
}
