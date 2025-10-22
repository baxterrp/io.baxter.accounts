package io.baxter.accounts.api.models;

import lombok.Getter;

@Getter
public class LoginResponse {
    String username;
    String token;

    public LoginResponse(String username, String token){
        this.username = username;
        this.token = token;
    }
}
