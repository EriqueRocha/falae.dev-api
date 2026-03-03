package dev.falae.application.exceptions;

public class AuthenticationException extends ApplicationException {

    private static final String ERROR_CODE = "AUTHENTICATION_FAILED";

    public AuthenticationException(String message) {
        super(message, ERROR_CODE);
    }

    public AuthenticationException() {
        super("Invalid credentials", ERROR_CODE);
    }
}