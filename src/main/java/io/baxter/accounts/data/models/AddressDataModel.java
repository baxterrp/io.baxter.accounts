package io.baxter.accounts.data.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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

    public AddressDataModel(Integer id, String street, String city, String state, String zip, String country){
        this.id = id;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.city = country;
    }

    public Integer getId() { return this.id; }
    public String getStreet() { return this.street; }
    public String getCity() { return this.city; }
    public String getState() { return this.state; }
    public String getZip() { return this.zip; }
    public String getCountry() { return this.country; }
}
