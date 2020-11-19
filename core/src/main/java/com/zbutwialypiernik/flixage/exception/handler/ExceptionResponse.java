package com.zbutwialypiernik.flixage.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {

    private final String message;

    private final int status;

}