package com.zbutwialypiernik.flixage.exception;

import org.springframework.http.HttpStatus;

public class ResourceForbiddenException extends ApiException {

    public ResourceForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Forbidden access");
    }

    public ResourceForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}