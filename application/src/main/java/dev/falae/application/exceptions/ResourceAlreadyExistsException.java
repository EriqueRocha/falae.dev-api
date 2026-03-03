package dev.falae.application.exceptions;

public class ResourceAlreadyExistsException extends ApplicationException {

    private static final String ERROR_CODE = "RESOURCE_ALREADY_EXISTS";

    public ResourceAlreadyExistsException(String message) {
        super(message, ERROR_CODE);
    }

    public ResourceAlreadyExistsException(String resourceName, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resourceName, field, value), ERROR_CODE);
    }
}