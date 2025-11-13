package io.baxter.accounts.infrastructure.messaging;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountRegisteredEvent {
    Integer id;
    UUID userId;
    String email;
}
