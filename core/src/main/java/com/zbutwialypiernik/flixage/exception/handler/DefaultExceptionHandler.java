package com.zbutwialypiernik.flixage.exception.handler;

import com.zbutwialypiernik.flixage.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Intent of this class is to hide possible vulnerability and implementation details
 * every exception that is not inherited from {@link ApiException} and not caught
 * will have malformed message to "Internal Server Error" without any details
 */
@Log4j2
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultExceptionHandler {

    private final Clock clock;

    @Autowired
    public DefaultExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ExceptionResponse> handle(Exception exception) {
        if (exception instanceof ApiException) {
            HttpStatus httpStatus = ((ApiException) exception).getStatus();

            return ResponseEntity.status(httpStatus)
                    .body(new ExceptionResponse(exception.getMessage(),
                            httpStatus.value(),
                            Instant.now(clock)));
        }

        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) exception;

            var errors = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest()
                    .body(new ValidationExceptionResponse(
                            "Validation error",
                            HttpStatus.BAD_REQUEST.value(),
                            Instant.now(clock),
                            errors));
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(new ExceptionResponse(
                            HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                            HttpStatus.METHOD_NOT_ALLOWED.value(),
                            Instant.now(clock)));
        }

        log.error("Unhandled internal exception: ", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ExceptionResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            Instant.now(clock)));
    }

}
