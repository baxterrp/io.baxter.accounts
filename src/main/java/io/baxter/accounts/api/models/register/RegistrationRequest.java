package io.baxter.accounts.api.models.register;

import io.baxter.accounts.api.models.AddressModel;
import io.baxter.accounts.api.models.PhoneModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    UUID userId;

    @NotEmpty(message = "firstName is required")
    @Size(max = 100, message = "firstName cannot exceed 100 characters")
    String firstName;

    @NotEmpty(message = "lastName is required")
    @Size(max = 100, message = "lastName cannot exceed 100 characters")
    String lastName;

    @Email(message = "invalid email format")
    @Size(max = 100, message = "email cannot exceed 100 characters")
    @NotEmpty(message = "email is required")
    String email;

    @Pattern(
            regexp = "^(?=.{8,}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$",
            message = "invalid password (at least 8 characters, 1 upper case, 1 lower case, 1 special character)")
    String password;

    PhoneModel phone;
    AddressModel address;
}
