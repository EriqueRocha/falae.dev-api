package dev.falae.application.exceptions;

public class ValidationException extends ApplicationException {

    private static final String ERROR_CODE = "VALIDATION_ERROR";

    public ValidationException(String message) {
        super(message, ERROR_CODE);
    }

    public ValidationException(String field, String reason) {
        super(String.format("Validation failed for field '%s': %s", field, reason), ERROR_CODE);
    }
}
