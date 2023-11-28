package com.tpa.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(value = {UserRequestException.class})
    public ResponseEntity<Object> handeUserRequestException(UserRequestException e) {
            HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

            UserException userException =  new UserException(
            e.getMessage(),
            httpStatus,
            ZonedDateTime.now(ZoneId.of("Europe/Warsaw"))
            );

            return new ResponseEntity<>(userException, httpStatus);
    }

}
