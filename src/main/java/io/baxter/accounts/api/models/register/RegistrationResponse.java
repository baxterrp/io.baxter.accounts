package io.baxter.accounts.api.models.register;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RegistrationResponse {
    Integer id;
    UUID userId;
    String email;
}
