package io.baxter.accounts.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Phone {
    private Integer id;
    private String number;
    private String countrycode;
}
