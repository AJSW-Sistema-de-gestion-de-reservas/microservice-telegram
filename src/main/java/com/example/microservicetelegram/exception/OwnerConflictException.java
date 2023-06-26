package com.example.microservicetelegram.exception;

public class OwnerConflictException extends RuntimeException {

    public OwnerConflictException() {
        super();
    }

    public OwnerConflictException(String message) {
        super(message);
    }

}
