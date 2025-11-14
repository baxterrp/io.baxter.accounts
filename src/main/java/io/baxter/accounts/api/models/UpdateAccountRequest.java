package io.baxter.accounts.api.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAccountRequest {
    @NotEmpty(message = "firstName is required")
    @Size(max = 100, message = "firstName cannot exceed 100 characters")
    String firstName;

    @NotEmpty(message = "lastName is required")
    @Size(max = 100, message = "lastName cannot exceed 100 characters")
    String lastName;

    PhoneModel phone;
    AddressModel address;
}
