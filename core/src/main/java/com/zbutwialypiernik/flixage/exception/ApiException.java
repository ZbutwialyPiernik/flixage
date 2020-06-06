package com.zbutwialypiernik.flixage.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Every exception that should be visible in public API has to inherit from
 * this class in other case message will be malformed and shown
 * as internal server error with http code 500.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
