package com.zbutwialypiernik.flixage.exception;

import lombok.Getter;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Intent of this class is to hide possible vulnerability and implementation details
 * every exception that is not inherited from {@link ApiException} and not caught
 * will have malformed message as "Internal Server Error"
 *
 * Also
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private final Clock clock;

    @Autowired
    public DefaultExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleEntityNotFound(Exception exception) {
        if (exception instanceof ApiException) {
            HttpStatus httpStatus = ((ApiException) exception).getStatus();

            return ResponseEntity.status(httpStatus)
                    .body(new ExceptionResponse(exception.getMessage(),
                            httpStatus.getReasonPhrase(),
                            httpStatus.value(),
                            LocalDateTime.now(clock)));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse("Internal Server Error",
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now(clock)));
    }

    @Getter
    private static class ExceptionResponse {
        private final String message;
        private final String error;
        private final int status;
        private final LocalDateTime timestamp;

        private ExceptionResponse(String message, String error, int status, LocalDateTime timestamp) {
            this.message = message;
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
        }
    }

}
