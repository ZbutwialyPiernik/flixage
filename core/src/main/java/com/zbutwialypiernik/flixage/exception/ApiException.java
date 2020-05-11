package com.zbutwialypiernik.flixage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Every exception in API has to inherit from this class in other case
 * message will be hidden and shown as internal server error 500.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
