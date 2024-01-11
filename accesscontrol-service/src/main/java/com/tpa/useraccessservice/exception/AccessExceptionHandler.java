package com.tpa.useraccessservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class AccessExceptionHandler {
    @ExceptionHandler(value = {AccessRequestException.class})
    public ResponseEntity<Object> handeUserRequestException(AccessRequestException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        AccessException accessException =  new AccessException(
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
        );

        return new ResponseEntity<>(accessException, httpStatus);
    }

    @ExceptionHandler(value = {AccessServerException.class})
    public ResponseEntity<Object> handeUserRequestException(AccessServerException e) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        AccessException accessException =  new AccessException(
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
        );

        return new ResponseEntity<>(accessException, httpStatus);
    }
}
