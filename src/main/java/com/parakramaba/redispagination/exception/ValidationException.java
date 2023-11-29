package com.parakramaba.redispagination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A ValidationException is thrown when a parameter value in the API call not in the expected format.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private static final long SerialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with null as its detail message.
     */
    public ValidationException() { super();}

    /**
     * Constructs a new ValidationException with specified detail message.
     * @param message Detail message. The message is saved and can be retrieved later, using getMessage() method
     */
    public ValidationException(String message) {
        super(message);
    }
}
