package io.baxter.accounts.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressModel {
    private Integer id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
}
