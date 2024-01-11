package com.tpa.useraccessservice.exception;

public class AccessServerException extends RuntimeException {

    public AccessServerException(String message) {
        super(message);
    }

    public AccessServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
