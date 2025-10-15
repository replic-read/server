package com.rere.server.inter.dispatching;

import com.rere.server.inter.dto.error.ErrorResponseInfo;
import com.rere.server.inter.dto.error.HttpErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Error handler for catching our own exceptions.
 */
@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles a {@link HttpErrorResponseException} by returning a response with the given error body.
     * @param ex The exception.
     * @return The response.
     */
    @ExceptionHandler(HttpErrorResponseException.class)
    public ResponseEntity<ErrorResponseInfo> handleHttpErrorResponseException(HttpErrorResponseException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ex.getError());
    }
}
