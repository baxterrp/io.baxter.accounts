package io.baxter.accounts.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAccountRequest {
    PhoneModel phone;
    AddressModel address;
}
