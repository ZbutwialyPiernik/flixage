package com.zbutwialypiernik.flixage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceForbiddenException extends ApiException {

    public ResourceForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Forbidden access");
    }

    public ResourceForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}