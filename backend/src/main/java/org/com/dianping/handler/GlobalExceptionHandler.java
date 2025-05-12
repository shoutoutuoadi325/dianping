package org.com.dianping.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler to handle exceptions across the application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
<<<<<<< HEAD
=======

    /**
     * Handles {@link RuntimeException} and returns a structured error response.
     *
     * @param e the runtime exception
     * @return a {@link ResponseEntity} containing the error message
     */
>>>>>>> origin/master
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
    }

<<<<<<< HEAD
    record ErrorResponse(String message) {}
=======
    /**
     * A record to encapsulate error response details.
     *
     * @param message the error message
     */
    record ErrorResponse(String message) {
    }
>>>>>>> origin/master
}