package com.zbutwialypiernik.flixage.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Resource not found");
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
