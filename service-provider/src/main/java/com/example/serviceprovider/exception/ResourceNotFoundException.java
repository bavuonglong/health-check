package com.example.serviceprovider.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResourceNotFoundException extends GeneralException {

    public ResourceNotFoundException(HttpStatus httpStatus, String errorCode, String devMessage) {
        super(httpStatus, errorCode, devMessage);
    }

}
