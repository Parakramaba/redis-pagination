package com.parakramaba.redispagination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * This GlobalExceptionHandler class provides set of methods to handle exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This handler method returns a ResponseEntity with the details of the ResourceNotFoundException.
     * @param ex ResourceNotFoundException object, not null
     * @param request WebRequest object, not null
     * @return Details of the exception, not null
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                             final WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND.value(),
                request.getDescription(false),
                ex.getMessage(),
                LocalDateTime.now(),
                null);

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
