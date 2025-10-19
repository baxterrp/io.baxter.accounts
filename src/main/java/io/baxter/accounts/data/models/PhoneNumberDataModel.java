package io.baxter.accounts.data.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "phone_numbers")
public class PhoneNumberDataModel {
    @Id
    public Integer id;

    @Column("number")
    public String number;

    @Column("countrycode")
    public String countrycode;

    public PhoneNumberDataModel(Integer id, String number, String countrycode){
        this.id = id;
        this.number = number;
        this.countrycode = countrycode;
    }
}
