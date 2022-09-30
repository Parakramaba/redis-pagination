package com.parakramaba.redispagination.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class holds the set of information that need to pass when an error occurred.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ErrorDetails implements Serializable {

    private int status;

    private String error;

    private String message;

    private LocalDateTime dateTime;

    private Object data;
}
