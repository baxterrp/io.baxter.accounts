package io.baxter.accounts.data.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table(name = "accounts")
public class AccountDataModel {
    @Id
    public Integer id;

    @Column("email")
    public String email;

    @Column("address_id")
    public Integer addressId;

    @Column("phone_id")
    public Integer phoneId;

    public AccountDataModel(Integer id, String email, Integer addressId, Integer phoneId){
        this.id = id;
        this.email = email;
        this.addressId = addressId;
        this.phoneId = phoneId;
    }

}
