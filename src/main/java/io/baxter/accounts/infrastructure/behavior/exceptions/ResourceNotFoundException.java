package io.baxter.accounts.infrastructure.behavior.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceType, String id){
        super(String.format("No %s found with id %s", resourceType, id));
    }
}
