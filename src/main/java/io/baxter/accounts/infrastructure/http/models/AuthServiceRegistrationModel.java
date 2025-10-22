package io.baxter.accounts.infrastructure.http.models;

import java.util.List;

public record AuthServiceRegistrationModel(String userName, String password, List<String> roles) {

}
