package com.restaurant.auth_service.core.exception;

public class InvalidFieldException extends RuntimeException {
    private final String field;
    private final String code;

    public InvalidFieldException(String field, String code, String message) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public String getField() { return field; }
    public String getCode() { return code; }
}
