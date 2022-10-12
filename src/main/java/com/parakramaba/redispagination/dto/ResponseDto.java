package com.parakramaba.redispagination.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto implements Serializable {

    private int status;

    private String error;

    private String message;

    private LocalDateTime dateTime;

    private Object data;
}
