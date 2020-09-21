package com.zbutwialypiernik.flixage.exception.handler;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class ValidationExceptionResponse extends ExceptionResponse {

    private final List<String> validationErrors;

    public ValidationExceptionResponse(String message, int status, Instant timestamp, List<String> validationErrors) {
        super(message, status, timestamp);
        this.validationErrors = validationErrors;
    }
}
