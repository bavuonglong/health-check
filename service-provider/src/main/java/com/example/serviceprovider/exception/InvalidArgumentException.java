package com.example.serviceprovider.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class InvalidArgumentException extends GeneralException {

    public InvalidArgumentException(HttpStatus httpStatus, String errorCode, String devMessage) {
        super(httpStatus, errorCode, devMessage);
    }
}