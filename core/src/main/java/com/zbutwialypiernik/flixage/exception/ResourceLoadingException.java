package com.zbutwialypiernik.flixage.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceLoadingException extends ApiException {

    public ResourceLoadingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}