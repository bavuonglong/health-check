package com.example.serviceprovider.controller.advice;

import com.example.serviceprovider.constant.ErrorCode;
import com.example.serviceprovider.dto.ServiceResponse;
import com.example.serviceprovider.exception.DuplicatedUrlException;
import com.example.serviceprovider.exception.ErrorResponseFactory;
import com.example.serviceprovider.exception.InvalidArgumentException;
import com.example.serviceprovider.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorResponseFactory errorResponseFactory;

    public GlobalExceptionHandler(ErrorResponseFactory errorResponseFactory) {
        this.errorResponseFactory = errorResponseFactory;
    }

    @ExceptionHandler({DuplicatedUrlException.class})
    public ResponseEntity<ServiceResponse> handleDuplicatedUrlException(DuplicatedUrlException exception) {
        return errorResponseFactory.generateErrorResponse(exception);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ServiceResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return errorResponseFactory.generateErrorResponse(exception);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ServiceResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return errorResponseFactory.generateErrorResponse(HttpStatus.BAD_REQUEST, ErrorCode.SEV_400_001.getValue(), e.getMessage());
    }

    @ExceptionHandler({InvalidArgumentException.class})
    public ResponseEntity<ServiceResponse> handleInvalidArgumentException(InvalidArgumentException exception) {
        return errorResponseFactory.generateErrorResponse(exception);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ServiceResponse> handleException(Exception exception) {
        log.error("Error occurs: [{}]", exception.getMessage());
        return errorResponseFactory.generateErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.SEV_500.getValue(), "Unknown Error");
    }
}
