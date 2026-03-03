package dev.falae.application.exceptions;

public class BusinessRuleException extends ApplicationException {

    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";

    public BusinessRuleException(String message) {
        super(message, ERROR_CODE);
    }

    public BusinessRuleException(String message, String customErrorCode) {
        super(message, customErrorCode);
    }
}