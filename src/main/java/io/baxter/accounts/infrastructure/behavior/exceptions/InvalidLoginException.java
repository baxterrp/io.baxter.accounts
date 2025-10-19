package io.baxter.accounts.infrastructure.behavior.exceptions;

public class InvalidLoginException extends RuntimeException{
    public InvalidLoginException(){
        super("Unauthorized");
    }
}
