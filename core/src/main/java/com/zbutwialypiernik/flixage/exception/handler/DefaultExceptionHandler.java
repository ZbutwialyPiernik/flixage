package com.zbutwialypiernik.flixage.exception.handler;

import com.zbutwialypiernik.flixage.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Intent of this class is to hide possible vulnerability and implementation details
 * every exception that is not inherited from {@link ApiException} or not mentioned in
 * @see <a href="https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/mvc.html#mvc-ann-rest-spring-mvc-exceptions"> Spring Docs <a/>
 *  and not caught will have malformed message to "Internal Server Error" without any details
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
    protected ResponseEntity<ExceptionResponse> handle(Exception exception, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        if (exception instanceof ApiException) {
            HttpStatus httpStatus = ((ApiException) exception).getStatus();

            return ResponseEntity.status(httpStatus)
                    .body(new ExceptionResponse(exception.getMessage(),
                            httpStatus.value(),
                            Instant.now(clock)));
        }

        ResponseEntity<ExceptionResponse> defaultMessage = handleDefaultExceptions(exception, request, response, handler);

        if (defaultMessage != null) {
            return defaultMessage;
        }

        log.error("Unhandled internal exception: ", exception);

        return defaultBody(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public ResponseEntity<ExceptionResponse> handleDefaultExceptions(Exception exception, HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return handleHttpRequestMethodNotSupported(
                    (HttpRequestMethodNotSupportedException) exception, request, response, handler);
        }
        else if (exception instanceof HttpMediaTypeNotSupportedException) {
            return handleHttpMediaTypeNotSupported(
                    (HttpMediaTypeNotSupportedException) exception, request, response, handler);
        }
        else if (exception instanceof HttpMediaTypeNotAcceptableException) {
            return handleHttpMediaTypeNotAcceptable(
                    (HttpMediaTypeNotAcceptableException) exception, request, response, handler);
        }
        else if (exception instanceof MissingPathVariableException) {
            return handleMissingPathVariable(
                    (MissingPathVariableException) exception, request, response, handler);
        }
        else if (exception instanceof MissingServletRequestParameterException) {
            return handleMissingServletRequestParameter(
                    (MissingServletRequestParameterException) exception, request, response, handler);
        }
        else if (exception instanceof ServletRequestBindingException) {
            return handleServletRequestBindingException(
                    (ServletRequestBindingException) exception, request, response, handler);
        }
        else if (exception instanceof ConversionNotSupportedException) {
            return handleConversionNotSupported(
                    (ConversionNotSupportedException) exception, request, response, handler);
        }
        else if (exception instanceof TypeMismatchException) {
            return handleTypeMismatch(
                    (TypeMismatchException) exception, request, response, handler);
        }
        else if (exception instanceof HttpMessageNotReadableException) {
            return handleHttpMessageNotReadable(
                    (HttpMessageNotReadableException) exception, request, response, handler);
        }
        else if (exception instanceof HttpMessageNotWritableException) {
            return handleHttpMessageNotWritable(
                    (HttpMessageNotWritableException) exception, request, response, handler);
        }
        else if (exception instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValidException(
                    (MethodArgumentNotValidException) exception, request, response, handler);
        }
        else if (exception instanceof MissingServletRequestPartException) {
            return handleMissingServletRequestPartException(
                    (MissingServletRequestPartException) exception, request, response, handler);
        }
        else if (exception instanceof BindException) {
            return handleBindException((BindException) exception, request, response, handler);
        }
        else if (exception instanceof NoHandlerFoundException) {
            return handleNoHandlerFoundException(
                    (NoHandlerFoundException) exception, request, response, handler);
        }
        else if (exception instanceof AsyncRequestTimeoutException) {
            return handleAsyncRequestTimeoutException(
                    (AsyncRequestTimeoutException) exception, request, response, handler);
        }

        return null;
    }

    protected ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                               HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED);

        if (ex.getSupportedHttpMethods() != null) {
            builder.allow(ex.getSupportedHttpMethods().toArray(new HttpMethod[0]));
        }

        return builder.body(new ExceptionResponse(
                        HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        Instant.now(clock)));
    }

    protected ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                           HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        ResponseEntity.BodyBuilder builder =  ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        if (!CollectionUtils.isEmpty(ex.getSupportedMediaTypes())) {
            builder.header(HttpHeaders.ACCEPT, MediaType.toString(ex.getSupportedMediaTypes()));
        }

        return builder.body(new ExceptionResponse(
                HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                Instant.now(clock)));
    }

    protected ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
                                                            HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        return defaultBody(HttpStatus.NOT_ACCEPTABLE, null);
    }

    protected ResponseEntity<ExceptionResponse> handleMissingPathVariable(MissingPathVariableException ex,
                                                     HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)  {

        return defaultBody(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    protected ResponseEntity<ExceptionResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {

        return defaultBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    protected ResponseEntity<ExceptionResponse> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        return defaultBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handle the case when a {@link org.springframework.web.bind.WebDataBinder} conversion cannot occur.
     * <p>The default implementation sends an HTTP 500 error, and returns an empty {@code ModelAndView}.
     * Alternatively, a fallback view could be chosen, or the ConversionNotSupportedException could be
     * rethrown as-is.
     * @param ex the ConversionNotSupportedException to be handled
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the executed handler
     * @return an empty ModelAndView indicating the exception was handled
     * @throws IOException potentially thrown from {@link HttpServletResponse#sendError}
     */
    protected ResponseEntity<ExceptionResponse> handleConversionNotSupported(ConversionNotSupportedException ex,
                                                        HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        log.warn("Unhandled exception", ex);

        return defaultBody(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    protected ResponseEntity<ExceptionResponse> handleTypeMismatch(TypeMismatchException ex,
                                              HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)  {
        return defaultBody(HttpStatus.BAD_REQUEST, null);
    }

    protected ResponseEntity<ExceptionResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                        HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)  {
        return defaultBody(HttpStatus.BAD_REQUEST, null);
    }

    protected ResponseEntity<ExceptionResponse> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
                                                        HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        log.warn("Unhandled exception", ex);

        return defaultBody(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    protected ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                 HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)  {

        final var errors= exception.getBindingResult()
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

    protected ResponseEntity<ExceptionResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex,
                                                                    HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        return defaultBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    protected ResponseEntity<ExceptionResponse> handleBindException(BindException ex, HttpServletRequest request,
                                               HttpServletResponse response, @Nullable Object handler) {
        return defaultBody(HttpStatus.BAD_REQUEST, null);
    }

    protected ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                         HttpServletRequest request, HttpServletResponse response, @Nullable Object handler)  {
        log.warn(ex.getMessage());

        return defaultBody(HttpStatus.NOT_FOUND, null);
    }

    protected ResponseEntity<ExceptionResponse> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex,
                                                              HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
        if (!response.isCommitted()) {
            return defaultBody(HttpStatus.SERVICE_UNAVAILABLE, null);
        }
        else {
            log.warn("Async request timed out", ex);
        }

        return null;
    }

    private ResponseEntity<ExceptionResponse> defaultBody(HttpStatus status, String message) {
        message = message != null ? message : status.getReasonPhrase();

        return ResponseEntity.status(status)
                .body(new ExceptionResponse(
                        message,
                        status.value(),
                        Instant.now(clock)));
    }

}
