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
@Table(name = "addresses")
public class AddressDataModel {
    @Id
    public Integer id;

    @Column("street")
    public String street;

    @Column("city")
    public String city;

    @Column("state")
    public String state;

    @Column("zip")
    public String zip;

    @Column("country")
    public String country;
}
