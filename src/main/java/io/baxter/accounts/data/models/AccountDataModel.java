package io.baxter.accounts.data.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class AccountDataModel {
    @Id
    private Integer id;

    @Column("user_id")
    private String userId;

    @Column("email")
    private String email;

    @Column("address_id")
    private Integer addressId;

    @Column("phone_id")
    private Integer phoneId;
}
