package com.example.serviceprovider.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class GeneralException extends RuntimeException {
    private HttpStatus httpStatus;
    private String errorCode;
    private String devMessage;

    public GeneralException(HttpStatus httpStatus, String errorCode, String devMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.devMessage = devMessage;
    }
}