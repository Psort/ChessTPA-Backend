package com.tpa.useraccessservice.exception;

public class AccessRequestException extends RuntimeException {

    public AccessRequestException(String message) {
        super(message);
    }

    public AccessRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
