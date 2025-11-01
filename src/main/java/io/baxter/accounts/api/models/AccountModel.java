package io.baxter.accounts.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountModel {
    Integer id;
    Integer userId;
    String email;
    PhoneModel phone;
    AddressModel address;
}
