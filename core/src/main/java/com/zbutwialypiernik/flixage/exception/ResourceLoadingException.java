package com.zbutwialypiernik.flixage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ResourceLoadingException extends RuntimeException {

    public ResourceLoadingException(String message) {
        super(message);
    }

}