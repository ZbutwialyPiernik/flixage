package com.zbutwialypiernik.flixage.exception.handler;

import com.zbutwialypiernik.flixage.exception.ApiException;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Clock;
import java.time.Instant;

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

        log.error("Unhandled internal exception: ", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ExceptionResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            Instant.now(clock)));
    }

}
