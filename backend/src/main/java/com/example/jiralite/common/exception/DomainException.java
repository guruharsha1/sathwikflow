package com.example.jiralite.common.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class DomainException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final Map<String, String> fieldErrors;

    private DomainException(HttpStatus status, String code, String message, Map<String, String> fieldErrors) {
        super(message);
        this.status = status;
        this.code = code;
        this.fieldErrors = fieldErrors;
    }
    public static DomainException notFound(String message) { return new DomainException(HttpStatus.NOT_FOUND, "NOT_FOUND", message, Map.of()); }
    public static DomainException forbidden(String message) { return new DomainException(HttpStatus.FORBIDDEN, "FORBIDDEN", message, Map.of()); }
    public static DomainException unauthorized(String message) { return new DomainException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", message, Map.of()); }
    public static DomainException conflict(String message) { return new DomainException(HttpStatus.CONFLICT, "CONFLICT", message, Map.of()); }
    public static DomainException validation(String message) { return new DomainException(HttpStatus.UNPROCESSABLE_ENTITY, "DOMAIN_VALIDATION", message, Map.of()); }
    public static DomainException validation(String message, Map<String, String> errors) { return new DomainException(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message, errors); }
    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}

