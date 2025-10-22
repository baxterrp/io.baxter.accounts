package io.baxter.accounts.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phone_numbers")
public class PhoneNumberDataModel {
    @Id
    public Integer id;

    @Column("number")
    public String number;

    @Column("countrycode")
    public String countrycode;
}
