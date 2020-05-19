package com.zbutwialypiernik.flixage.exception.handler;

import lombok.Value;

import java.time.Instant;

@Value
public class ExceptionResponse {
    String message;

    int status;

    Instant timestamp;

}