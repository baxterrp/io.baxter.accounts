package io.baxter.accounts.infrastructure.behavior.exceptions;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException() {
        super("Unauthorized");
    }
}
