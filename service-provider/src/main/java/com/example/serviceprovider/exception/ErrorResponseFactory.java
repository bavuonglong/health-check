package com.example.serviceprovider.exception;

import com.example.serviceprovider.dto.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Component
@Slf4j
public class ErrorResponseFactory {

    private static final String TITLE = "TTL.";
    private static final String MESSAGE = "MSG.";
    private static final String DEFAULT_MESSAGE = "";

    private final MessageSource messageSource;

    public ErrorResponseFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public ResponseEntity<ServiceResponse> generateErrorResponse(GeneralException exception) {
        return generateErrorResponse(exception.getHttpStatus(), exception.getErrorCode(), exception.getDevMessage());
    }

    public ResponseEntity<ServiceResponse> generateErrorResponse(HttpStatus httpStatus, String errorCode, String devMessage) {
        String title = messageSource.getMessage(TITLE + errorCode, null, DEFAULT_MESSAGE, Locale.getDefault());
        String message = messageSource.getMessage(MESSAGE + errorCode, null, DEFAULT_MESSAGE, Locale.getDefault());
        ServiceResponse serviceResponse = ServiceResponse.builder()
                .title(StringUtils.hasLength(title) ? null : title)
                .message(StringUtils.hasLength(message) ? null : message)
                .code(errorCode)
                .developerMessage(devMessage)
                .build();

        return ResponseEntity
                .status(httpStatus)
                .body(serviceResponse);
    }
}
