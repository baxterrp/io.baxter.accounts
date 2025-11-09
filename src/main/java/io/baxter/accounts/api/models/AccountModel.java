package io.baxter.accounts.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AccountModel {
    Integer id;
    UUID userId;
    String email;
    PhoneModel phone;
    AddressModel address;
}
