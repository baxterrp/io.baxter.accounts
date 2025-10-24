package io.baxter.accounts.api.models.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "invalid email format")
    @Size(max = 100, message = "email cannot exceed 100 characters")
    @NotEmpty(message = "email is required")
    String email;

    @Pattern(
            regexp = "^(?=.{8,}$)(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9])\\S+$",
            message = "invalid password (at least 8 characters, 1 upper case, 1 lower case, 1 special character)")
    String password;
}